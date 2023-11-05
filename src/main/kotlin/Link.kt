import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

fun main() = application {

	val defaultList = arrayListOf<Offset>()
	repeat(50){
		defaultList.add(Offset(it.toFloat(),(0..4).random().toFloat()))
	}

	val dataFlow = MutableStateFlow(defaultList.toList())
	val data by dataFlow.collectAsState()
	LaunchedEffect(Unit){
		while(true){
			// 删除第一位并添加后一位
			val list = arrayListOf<Offset>().apply {
				addAll(data)
				removeFirst()
				add(Offset(last().x+1,(0 until  4).random().toFloat()))
			}
			// 全体元素向前移动一位
			val offsets = list.map { Offset(it.x - 1, it.y) }
			dataFlow.emit(offsets)
			delay(500L)
		}

	}
	Window(onCloseRequest = ::exitApplication) {
		LineChart(data = data)
	}
}