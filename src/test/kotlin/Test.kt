import javax.usb.UsbDevice
import javax.usb.UsbEndpoint
import javax.usb.UsbHostManager
import javax.usb.UsbInterface
import kotlin.concurrent.thread

lateinit var printUsbDevice: UsbDevice
fun main() {
	val rootUsbHub = UsbHostManager.getUsbServices().rootUsbHub
	rootUsbHub.attachedUsbDevices.forEach {
		if (it is UsbDevice) {
			val descriptor = it.usbDeviceDescriptor
			// 设备管理器中,具体USB >> 属性 >> 硬件ID >>  对应的 USB\VID_{vid}&PID_{pid}(16进制)
			// 输出时0会被去掉,无关紧要
			val vid = descriptor.idVendor().toString(16).uppercase()
			val pid = descriptor.idProduct().toString(16).uppercase()
			println("vid:$vid\tpid:$pid")
			if (vid == "483" && pid == "5740") {
				printUsbDevice = it
				return@forEach
			}
		}
	}
	if (!::printUsbDevice.isInitialized) {
		println("未找到对应设备")
		return
	}
	val usbInterfaces = printUsbDevice.activeUsbConfiguration.usbInterfaces
	if (usbInterfaces.isEmpty()) {
		println("此配置没有usb接口")
		return
	}
	val interFace = usbInterfaces[0]
	if (interFace !is UsbInterface) return
	// 已连接则先关闭再重新连接
	if (interFace.isClaimed) {
		interFace.release()
	}
	interFace.claim { true }
	val size = interFace.usbEndpoints.size
	if (size <= 0) return
	val endpoint1 = interFace.usbEndpoints[0]
	var readUsbEndpoint = interFace.usbEndpoints[1]
	if (endpoint1 !is UsbEndpoint) return
	if (!endpoint1.usbEndpointDescriptor.toString().contains("OUT")) {
		readUsbEndpoint = endpoint1
	}
	if (readUsbEndpoint !is UsbEndpoint) return
	val readPipe = readUsbEndpoint.usbPipe
	if (!readPipe.isOpen) {
		readPipe.open()
	}

	try {
		thread {
			val buffer = ByteArray(64)
			while (true) {
				val submit = readPipe.syncSubmit(buffer)
				println(submit)
			}
		}
	} catch (e: Exception) {
		e.printStackTrace()
	} finally {
		interFace.release()
	}

}
