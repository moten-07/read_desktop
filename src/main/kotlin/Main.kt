import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

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

	MaterialTheme {
		Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {

		}
	}
}
