import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.floor
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.roundToInt


fun xCoordination(width: Float, x: Float, totalCount: Int): Float {
	return width * (x / (totalCount - 1))
}

fun yCoordination(height: Float, y: Float, maxValue: Float): Float {
	return height - y * (height / maxValue)
}


/**
 * @param data 折线图的数据
 */
@Composable
fun LineChart(
	data: List<Offset> = listOf()
) {
	if (data.isEmpty()) return
	Card(
		shape = RoundedCornerShape(2),
		modifier = Modifier
			.fillMaxWidth()
			.padding(15.dp)
	) {
		var position by remember {
			mutableStateOf(Offset(0f, 0f))
		}

		Canvas(modifier = Modifier
			.fillMaxSize()
			.pointerInput(Unit) {
				detectTapGestures(onPress = { offset ->
					position = offset
					delay(5000L)
				})
			}) {


			val maxValue = data.maxOf { d -> d.y }

			drawYLabels(maxValue)

			val path = Path()
			path.moveTo(
				xCoordination(size.width, data[0].x, data.size),
				yCoordination(size.height, data[0].y, maxValue)
			)
//			var controlX = xCoordination(size.width, data[0].x, data.size)
//			var controlY = yCoordination(size.height, data[0].y, maxValue)
			data.forEachIndexed second@{ index1, dataPoint ->
				if (index1 == 0) return@second
				val endX = xCoordination(size.width, dataPoint.x, data.size)
				val endY = yCoordination(size.height, dataPoint.y, maxValue)
				path.lineTo(endX, endY)
				// 贝塞尔曲线
//				val endX = (controlX + xCoordination(size.width, dataPoint.x, data.size)) / 2
//				val endY = (controlY + yCoordination(size.height, dataPoint.y, maxValue)) / 2
//				path.quadraticBezierTo(controlX, controlY, endX, endY)
//				controlX = xCoordination(size.width, dataPoint.x, data.size)
//				controlY = yCoordination(size.height, dataPoint.y, maxValue)
			}
			drawPath(
				path,
				color = Color.Red,
				style = Stroke(width = 4f, cap = StrokeCap.Round)
			)
//			path.lineTo(size.width, size.height)
//			path.lineTo(
//				xCoordination(size.width, data[0].x, data.size),
//				size.height
//			)
			path.close()
			// 绘制成封闭区间会好看点
//			drawPath(
//				path,
//				brush = Brush.verticalGradient(
//					colors = listOf(
//						Color.Red,
//						Color.Transparent
//					)
//				)
//			)


		}
	}
}

/**
 * 绘制纵轴
 */
fun DrawScope.drawYLabels(maxValue: Float) {
	val yLabels: List<Int>
	var power = floor(log(maxValue, 10f)).toInt()
	var factor = maxValue / 10.0.pow(power.toDouble())
	if (factor < 4) {
		factor *= 10
		power -= 1
		yLabels = (1..7).map {
			(factor / 8 * it).roundToInt()
		}
	} else {
		yLabels = (1..factor.toInt()).map {
			(factor / (factor.toInt() + 1) * it).roundToInt()
		}
	}

	val yPositions: List<Float> =
		yLabels.map {
			yCoordination(
				size.height,
				it * 10.0.pow(power.toDouble()).toFloat(),
				maxValue
			)
		}
	yLabels.forEachIndexed { yLabelIndex, _ ->
		drawLine(
			brush = Brush.horizontalGradient(
				listOf(
					Color.LightGray,
					Color.LightGray
				)
			),
			start = Offset(0f, yPositions[yLabelIndex]),
			end = Offset(size.width, yPositions[yLabelIndex]),
			pathEffect = PathEffect.dashPathEffect(floatArrayOf(maxValue, maxValue))
		)
	}
}