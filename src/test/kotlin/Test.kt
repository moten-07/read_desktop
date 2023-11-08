import org.hid4java.HidManager
import org.hid4java.HidServicesSpecification

/**
 * USB-HID
 */
fun main() {
	val hidServicesSpecification = HidServicesSpecification().apply {
		isAutoStart = true
	}
	val hidServices = HidManager.getHidServices(hidServicesSpecification)
	hidServices.start()
	hidServices.scan()
	hidServices.attachedHidDevices.forEach {
		val vId = it.vendorId
		val pId = it.productId
		println("VID: ${vId.toString(16)}\tPID: ${pId.toString(16)}")
	}
	hidServices.stop()

}