import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import gnu.io.NRSerialPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.DataInputStream

@Composable
fun App() {
	val serialState = MutableStateFlow(NRSerialPort(""))
	val serial by serialState.collectAsState()

	MaterialTheme {
		Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {
			Left(serialState = serialState)
			// 中间分割线
			Right(serial = serial)
		}
	}
}

/**
 * 单个窗口
 */
@Composable
@Preview
fun RightView(modifier: Modifier) {
	Box(
		modifier = modifier.border(
			width = 1.dp,
			color = Color.Black,
			shape = RoundedCornerShape(16.dp)
		)
			.padding(4.dp)
	) {

	}
}

/**
 * 右侧多窗口
 */
@Composable
fun Right(serial: NRSerialPort) {
	val scope = rememberCoroutineScope()
	val readCharState = MutableStateFlow("")
	scope.launch {

		if (!serial.isConnected) {
			return@launch
		}
		val ins = DataInputStream(serial.inputStream)
		while (!Thread.interrupted()) {
			if (ins.available() > 0) {
				val b = ins.read().toString()
				readCharState.emit(b)
			}
			delay(5)
		}
	}
	LazyVerticalGrid(
		columns = GridCells.Fixed(2),
		modifier = Modifier.fillMaxSize()
	) {
		items(count = 4) {
			RightView(modifier = Modifier.fillMaxSize().heightIn(min = 368.dp))
		}
	}
}

/**
 * 左侧串口列表
 */
@Composable
private fun Left(serialState: MutableStateFlow<NRSerialPort>) {
	val stateFlow = MutableStateFlow(NRSerialPort.getAvailableSerialPorts().toList())
	val scope = rememberCoroutineScope()
	val list by stateFlow.collectAsState()
	LazyColumn(
		modifier = Modifier.fillMaxHeight()
	) {
		item {
			Button(onClick = {
				scope.launch {
					stateFlow.emit(NRSerialPort.getAvailableSerialPorts().toList())
				}
			}) {
				Text(text = "refresh")
			}
		}
		items(list) {
			Port(serialState = serialState, name = it)
		}
	}
}

@Composable
fun Port(
	serialState: MutableStateFlow<NRSerialPort>,
	name: String,
	scope: CoroutineScope = rememberCoroutineScope(),
	modifier: Modifier = Modifier
) {
	Button(
		onClick = {
			scope.launch {
				val nrSerialPort = NRSerialPort(name).apply {
					if (!isConnected) {
						connect()
					}
				}
				serialState.emit(nrSerialPort)
			}
		},
		modifier = modifier
	) {
		Text(text = name)
	}
}

fun main() = application {
	val windowState = rememberWindowState(size = DpSize(width = 1024.dp, height = 800.dp))

	Window(
		state = windowState,
		onCloseRequest = ::exitApplication,
		icon = painterResource("icon.png"),
		title = "串口读取"
	) {
		App()
	}
}
