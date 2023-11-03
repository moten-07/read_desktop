import javax.usb.UsbDevice
import javax.usb.UsbEndpoint
import javax.usb.UsbHostManager
import javax.usb.UsbInterface
import kotlin.concurrent.thread

lateinit var printUsbDevice: UsbDevice
fun main() {
	val rootUsbHub = UsbHostManager.getUsbServices().rootUsbHub
	// M108 - 8E:E0:04:2A:B8:EE
	val vid: Short = 0x0483
	val pid: Short = 0x5740
	rootUsbHub.attachedUsbDevices.forEach {
		if (it is UsbDevice) {
			val descriptor = it.usbDeviceDescriptor
			// 设备管理器中,具体USB >> 属性 >> 硬件ID >>  对应的 USB\VID_{vid}&PID_{pid}(16进制)
			val idVendor = descriptor.idVendor().toString(16).uppercase()
			val idProduct = descriptor.idProduct().toString(16).uppercase()
			println("device vid:$idVendor, pid: $idProduct")
			if (vid == descriptor.idVendor() && pid == descriptor.idProduct()) {
				printUsbDevice = it
				return@forEach
			}
		}
	}
	if (!::printUsbDevice.isInitialized) {
		println("No corresponding device found")
		return
	}
	val usbInterfaces = printUsbDevice.activeUsbConfiguration.usbInterfaces
	if (usbInterfaces.isEmpty()) {
		println("This configuration does not have a usb interface")
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
			val buffer = ByteArray(4)
			while (true) {
				val bytes = readPipe.asyncSubmit(buffer).data
				bytes.map {
					val char = it.toInt()
					print(char)
				}
				println()
				Thread.sleep(1000)
			}
		}
	} catch (e: Exception) {
		e.printStackTrace()
	} finally {
		interFace.release()
	}

}
