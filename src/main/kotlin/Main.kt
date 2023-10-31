import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
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
import gnu.io.NRSerialPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.DataInputStream


@Composable
@Preview
fun App() {

	val stateFlow = MutableStateFlow(NRSerialPort.getAvailableSerialPorts().toList())
	val scope = rememberCoroutineScope()
	val list by stateFlow.collectAsState()

	MaterialTheme {
		stateFlow.collectAsState(initial = NRSerialPort.getAvailableSerialPorts().toList())
		LazyColumn {
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
				Port(it)
			}
		}
	}
}

@Composable
fun Port(
	name: String,
	scope: CoroutineScope = rememberCoroutineScope(),
	modifier: Modifier = Modifier
) {
	Button(
		onClick = {
			scope.launch {
				val serial = NRSerialPort(name)
				serial.connect()
				val ins = DataInputStream(serial.inputStream)
				while (!Thread.interrupted()) {
					if (ins.available() > 0) {
						val b = ins.read().toChar()
						print(b)
					}
					delay(5)
				}
			}
		},
		modifier = modifier
	) {
		Text(text = name)
	}
}

fun main() = application {
	val windowState = rememberWindowState(size = DpSize(600.dp, 600.dp))

	Window(
		state = windowState,
		onCloseRequest = ::exitApplication,
		icon = painterResource("icon.png"),
		title = "串口读取"
	) {
		App()
	}
}
