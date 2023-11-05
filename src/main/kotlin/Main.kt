import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
	val windowState =
		rememberWindowState(size = DpSize(width = 1200.dp, height = 720.dp))

	Window(
		state = windowState,
		onCloseRequest = ::exitApplication,
		icon = painterResource("icon.png"),
		title = "ReadForUSB"
	) {
		val ratio = windowState.size.width / windowState.size.height
		UsbApp(modifier = Modifier.aspectRatio(ratio = ratio))
	}
}

@Composable
fun UsbApp(modifier: Modifier = Modifier) {

	val usbDeviceFlow = MutableStateFlow<UsbDevice>(NullDevice())
	val usbDevice by usbDeviceFlow.collectAsState()
	val colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
	MaterialTheme(colors = colors) {
		Row(modifier = modifier.fillMaxSize()) {
			Left(
				modifier = Modifier.weight(1f),
				usbDeviceFlow = usbDeviceFlow
			)
			Box(
				modifier = Modifier.fillMaxHeight()
					.width(1.dp)
					.background(color = Color.Black)
			)
			Right(
				modifier = Modifier.weight(3f),
				usbDevice = usbDevice
			)
		}
	}
}

@Composable
fun Right(
	modifier: Modifier = Modifier,
	usbDevice: UsbDevice
) {
	Column(modifier = modifier.fillMaxSize()) {
		Row(modifier = Modifier.weight(1f)) {
			LineChart(
				modifier = Modifier.weight(1f),
				data = listOf(
					Offset(0F, (0..4).random().toFloat()),
					Offset(1F, (0..4).random().toFloat()),
					Offset(2F, (0..4).random().toFloat()),
					Offset(3F, (0..4).random().toFloat()),
					Offset(4F, (0..4).random().toFloat()),
					Offset(5F, (0..4).random().toFloat())
				)
			)
			LineChart(
				modifier = Modifier.weight(1f),
				data = listOf(
					Offset(0F, (0..4).random().toFloat()),
					Offset(1F, (0..4).random().toFloat()),
					Offset(2F, (0..4).random().toFloat()),
					Offset(3F, (0..4).random().toFloat()),
					Offset(4F, (0..4).random().toFloat()),
					Offset(5F, (0..4).random().toFloat())
				)
			)
		}
		Row(modifier = Modifier.weight(1f)) {
			LineChart(
				modifier = Modifier.weight(1f),
				data = listOf(
					Offset(0F, (0..4).random().toFloat()),
					Offset(1F, (0..4).random().toFloat()),
					Offset(2F, (0..4).random().toFloat()),
					Offset(3F, (0..4).random().toFloat()),
					Offset(4F, (0..4).random().toFloat()),
					Offset(5F, (0..4).random().toFloat())
				)
			)
			LineChart(
				modifier = Modifier.weight(1f),
				data = listOf(
					Offset(0F, (0..4).random().toFloat()),
					Offset(1F, (0..4).random().toFloat()),
					Offset(2F, (0..4).random().toFloat()),
					Offset(3F, (0..4).random().toFloat()),
					Offset(4F, (0..4).random().toFloat()),
					Offset(5F, (0..4).random().toFloat())
				)
			)
		}
	}
}

@Composable
fun Left(
	modifier: Modifier = Modifier,
	usbDeviceFlow: MutableStateFlow<UsbDevice>,
	scope: CoroutineScope = rememberCoroutineScope(),
) {
	val devicesFlow = MutableStateFlow(UsbHostManager.getUsbServices().rootUsbHub.attachedUsbDevices.toList())
	val list by devicesFlow.collectAsState()
	LazyColumn(modifier = modifier) {
		item {
			Text(text = "下列设备列表与设备管理器中USB控制器详细属性相同,转位数时自动去0,不影响实际使用")
		}
		items(items = list) {
			if (it !is UsbDevice) return@items
			val idVendor = it.usbDeviceDescriptor.idVendor().toString(16).uppercase()
			val idProduct = it.usbDeviceDescriptor.idProduct().toString(16).uppercase()
			Text(text = "USB\\VID_ $idVendor &PID_ $idProduct", modifier = Modifier.clickable {
				scope.launch {
					// 接口列表
					val usbInterfaces = it.activeUsbConfiguration?.usbInterfaces
					usbDeviceFlow.emit(it)
				}
			})
		}
	}
}
