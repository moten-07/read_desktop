import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.usb.UsbDevice
import javax.usb.UsbEndpoint
import javax.usb.UsbHostManager
import javax.usb.UsbInterface

fun main() = application {
	val windowState = rememberWindowState(size = DpSize(width = 1200.dp, height = 720.dp))


	val usbDeviceFlow = MutableStateFlow<UsbDevice>(NullDevice())
	val usbDevice by usbDeviceFlow.collectAsState()

	// 数据获取请在此处,否则会一直画(Compose重绘原理)

	val dataFlow1 = MutableStateFlow(listOf<Offset>())
	val data1 by dataFlow1.collectAsState()
	val dataFlow2 = MutableStateFlow(listOf<Offset>())
	val data2 by dataFlow2.collectAsState()
	val dataFlow3 = MutableStateFlow(listOf<Offset>())
	val data3 by dataFlow3.collectAsState()
	val dataFlow4 = MutableStateFlow(listOf<Offset>())
	val data4 by dataFlow4.collectAsState()


	val scope = rememberCoroutineScope()
	scope.launch {
		// 随机数据
//		repeat(100) {
//			processingData(data = data1, dataFlow = dataFlow1)
//			processingData(data = data2, dataFlow = dataFlow2)
//			processingData(data = data3, dataFlow = dataFlow3)
//			processingData(data = data4, dataFlow = dataFlow4)
//		}

		usbDeviceFlow.collect {
			println("=======")
			if (it is NullDevice) return@collect
			// 接口列表
			val usbInterfaces = it.activeUsbConfiguration?.usbInterfaces ?: return@collect
			if (usbInterfaces.isEmpty()) return@collect
			val interFace = usbInterfaces[0]
			if (interFace !is UsbInterface) return@collect
			// 已连接则需重新连接,避免异常
			if (interFace.isClaimed) {
				interFace.release()
			}
			interFace.claim { true }
			if (interFace.usbEndpoints.size <= 0) return@collect
			val endpoint = interFace.usbEndpoints[0]
			var readEndpoint = interFace.usbEndpoints[1]
			if (endpoint !is UsbEndpoint) return@collect
			// 不为输出通道则为读取通道
			if (!endpoint.usbEndpointDescriptor.toString().contains("OUT")) {
				readEndpoint = endpoint
			}
			if (readEndpoint !is UsbEndpoint) return@collect
			val usbPipe = readEndpoint.usbPipe.apply {
				if (!isOpen) {
					open()
				}
			}
			var index = 0
			while (true) {
				val bytes = usbPipe.asyncSubmit(ByteArray(4)).data
				print("${index++}==>")
				bytes.map { byte ->
					print(byte.toInt())
				}
				println()
				delay(1000L)
			}
		}


	}

	Window(
		state = windowState,
		onCloseRequest = ::exitApplication,
		icon = painterResource("icon.png"),
		title = "ReadForUSB"
	) {
		val ratio = windowState.size.width / windowState.size.height
		UsbApp(
			modifier = Modifier.aspectRatio(ratio = ratio),
			usbDeviceFlow = usbDeviceFlow,
			data = Data(data1, data2, data3, data4)
		)
	}
}

/**
 * [data] 数据
 * [maxPort] 单窗格显示的点位大小
 * [dataFlow] 数据流
 *
 * 数据流处理: 超出时移除第一个点位并整体往前移动
 */
private suspend fun processingData(
	data: List<Offset>,
	maxPort: Int = 30,
	dataFlow: MutableStateFlow<List<Offset>>
) {
	val list = arrayListOf<Offset>().let {
		it.addAll(data)
		it.add(Offset(it.size.toFloat(), (0..4).random().toFloat()))
		if (it.size > maxPort) {
			it.removeFirst()
			return@let it.map { offset -> Offset(offset.x - 1, offset.y) }
		}
		return@let it
	}
	dataFlow.emit(list)
	delay(100L)
}

@Composable
fun UsbApp(
	modifier: Modifier = Modifier,
	usbDeviceFlow: MutableStateFlow<UsbDevice>,
	data: Data
) {

	val colors = lightColors()
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
				data = data
			)
		}
	}
}

@Composable
fun Right(
	modifier: Modifier = Modifier,
	data: Data
) {
	val (data1, data2, data3, data4) = data
	Column(modifier = modifier.fillMaxSize()) {
		Row(modifier = Modifier.weight(1f)) {
			LineChart(
				modifier = Modifier.weight(1f),
				data = data1,
				lineColor = Color.Red
			)
			LineChart(
				modifier = Modifier.weight(1f),
				data = data2,
				lineColor = Color.Green
			)
		}
		Row(modifier = Modifier.weight(1f)) {
			LineChart(
				modifier = Modifier.weight(1f),
				data = data3,
				lineColor = Color.Blue
			)
			LineChart(
				modifier = Modifier.weight(1f),
				data = data4,
				lineColor = Color.Yellow
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
			Button(onClick = {
				scope.launch {
					devicesFlow.emit(UsbHostManager.getUsbServices().rootUsbHub.attachedUsbDevices.toList())
				}
			}) {
				Text(text = "刷新")
			}
		}
		// 下列设备列表与设备管理器中USB控制器详细属性相同,转位数时自动去0,不影响实际使用
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
