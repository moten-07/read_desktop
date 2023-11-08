package model

import model.message.Message
import javax.usb.UsbEndpoint
import javax.usb.UsbException
import javax.usb.UsbInterface


/**
 * 打印机设备
 * @param [interFace]   声明的USB打印机接口
 * @param [inEndpoint] 输入端点
 * @param [outEndpoint] 输出端点
 */
class PrintDevice(
	private val interFace: UsbInterface,
	private val inEndpoint: Byte,
	private val outEndpoint: Byte
) {

	/**
	 * 打开设备，当通信完成后应调用[close]方法
	 *
	 * @throws UsbException 当设备无法打开时。
	 */
	@Throws(UsbException::class)
	fun open() = interFace.claim()

	/**
	 * 关闭设备
	 */
	@Throws(UsbException::class)
	fun close() = interFace.release()

	/**
	 * 接收打印机数据
	 *
	 * @return 收到的数据
	 */
	@Throws(UsbException::class)
	fun receiveMessage(): Message {
		val inEndpoint: UsbEndpoint = interFace.getUsbEndpoint(inEndpoint)
		val inPipe = inEndpoint.usbPipe
		inPipe.open()
		try {
			val headerBytes = ByteArray(MessageHeader.SIZE)
			var received = inPipe.syncSubmit(headerBytes)
			if (received != MessageHeader.SIZE)
				throw InvalidMessageException("Invalid ADB message header size: $received")
			val header = MessageHeader(headerBytes)
			if (!header.isValid)
				throw InvalidMessageException("ADB message header checksum failure")
			val data = ByteArray(header.dataLength)
			received = inPipe.syncSubmit(data)
			if (received != header.dataLength)
				throw InvalidMessageException("ADB message data size mismatch. Should be ${header.dataLength} but is $received")
			val message: Message = Message.create(header, data)
			if (!message.isValid)
				throw InvalidMessageException("ADB message data checksum failure")
			return message
		} finally {
			inPipe.close()
		}
	}
}