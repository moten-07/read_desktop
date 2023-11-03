import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.usb.UsbDevice
import javax.usb.UsbHostManager

fun main() = application {
	val windowState = rememberWindowState(size = DpSize(width = 1024.dp, height = 800.dp))

	Window(
		state = windowState,
		onCloseRequest = ::exitApplication,
		icon = painterResource("icon.png"),
		title = "ReadForUSB"
	) {
		UsbApp()
	}
}

@Composable
fun UsbApp() {

	val usbDeviceFlow = MutableStateFlow<UsbDevice>(NullDevice())
	val usbDevice by usbDeviceFlow.collectAsState()

	MaterialTheme {
		Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {
			Life(usbDeviceFlow = usbDeviceFlow)
			Right(usbDevice = usbDevice)
		}
	}
}

@Composable
fun Right(usbDevice: UsbDevice) {

}

@Composable
fun Life(
	usbDeviceFlow: MutableStateFlow<UsbDevice>,
	scope: CoroutineScope = rememberCoroutineScope(),
) {
	val devicesFlow = MutableStateFlow(UsbHostManager.getUsbServices().rootUsbHub.attachedUsbDevices.toList())
	val list by devicesFlow.collectAsState()
	LazyColumn {
		item {
			Text(text = "下列设备列表与设备管理器中USB控制器详细属性相同,转位数时自动去0,不影响实际使用")
		}
		items(items = list) {
			if (it !is UsbDevice) return@items
			val idVendor = it.usbDeviceDescriptor.idVendor().toString(16).uppercase()
			val idProduct = it.usbDeviceDescriptor.idProduct().toString(16).uppercase()
			Text(text = "USB\\VID_ $idVendor &PID_ $idProduct", modifier = Modifier.clickable {
				scope.launch {
					usbDeviceFlow.emit(it)
				}
			})
		}
	}
}
