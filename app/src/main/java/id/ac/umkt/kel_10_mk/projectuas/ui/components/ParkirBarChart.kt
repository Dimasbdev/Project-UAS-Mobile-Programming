package id.ac.umkt.kel_10_mk.projectuas.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.ac.umkt.kel_10_mk.projectuas.ParkingStatus
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirAccent
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDanger
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDivider
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextSecondary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirWarning

data class ChartDataPoint(
    val label: String,
    val status: ParkingStatus
)

@Composable
fun ParkirBarChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Y-Axis Labels
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("PENUH", color = ParkirTextSecondary, fontSize = 10.sp)
                Text("SEDANG", color = ParkirTextSecondary, fontSize = 10.sp)
                Text("SEPI", color = ParkirTextSecondary, fontSize = 10.sp)
                Text("", color = ParkirTextSecondary, fontSize = 10.sp) // Base line
            }

            // Chart Canvas
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // Draw Grid Lines (4 lines)
                    val lineSpacing = canvasHeight / 3
                    for (i in 0..3) {
                        val y = i * lineSpacing
                        drawLine(
                            color = ParkirDivider,
                            start = Offset(0f, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = 1f
                        )
                    }

                    // Draw Bars
                    if (data.isNotEmpty()) {
                        val barWidth = 14.dp.toPx()
                        val totalSpacing = canvasWidth - (barWidth * data.size)
                        val spacing = totalSpacing / (data.size + 1)

                        data.forEachIndexed { index, point ->
                            val x = spacing + (index * (barWidth + spacing))

                            val barColor = when (point.status) {
                                ParkingStatus.PENUH -> ParkirDanger
                                ParkingStatus.SEDANG -> ParkirWarning
                                ParkingStatus.SEPI -> ParkirAccent
                            }

                            val targetHeight = when (point.status) {
                                ParkingStatus.PENUH -> canvasHeight
                                ParkingStatus.SEDANG -> canvasHeight * (2f / 3f)
                                ParkingStatus.SEPI -> canvasHeight * (1f / 3f)
                            }

                            val y = canvasHeight - targetHeight

                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, targetHeight),
                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )
                        }
                    }
                }
            }
        }

        // X-Axis Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 36.dp), // offset by Y-axis width approx
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { point ->
                Text(
                    text = point.label,
                    color = ParkirTextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
