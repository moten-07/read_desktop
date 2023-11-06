import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

fun main() = application {

	val dataFlow = MutableStateFlow(listOf<Offset>())
	val data by dataFlow.collectAsState()
	rememberCoroutineScope().launch {
		repeat(100) {
			// 删除第一位并添加后一位
			val list = arrayListOf<Offset>().let {
				it.addAll(data)
				it.add(Offset(it.size.toFloat(), (0..4).random().toFloat()))
				if (it.size > 50) {
					it.removeFirst()
					return@let it.map { offset -> Offset(offset.x - 1, offset.y) }
				}
				return@let it
			}
			dataFlow.emit(list)
			delay(100L)
		}

	}
	Window(onCloseRequest = ::exitApplication) {
		LineChart(data = data)
	}
}