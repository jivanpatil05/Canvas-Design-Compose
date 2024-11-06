package com.example.canvasdesigncompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canvasdesigncompose.ui.theme.CanvasDesignComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CanvasDesignComposeTheme {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(Color.Gray)
                ){
                    HorizontalProgressIndicator(
                        label = "Protein",
                        currentValue = 100,
                        maxValue = 200,
                        barColor = Color.Blue
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalProgressIndicator(
                        label = "Protein",
                        currentValue = 1500,
                        maxValue = 200,
                        barColor = Color.Cyan
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        BorderProgressIndicator(
                            currentValue = 800f,
                            maxValue = 1000f,
                            progressColor = Color.Blue,
                            sheetCollapsed = true
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        BorderProgressIndicator(
                            currentValue = 300f,
                            maxValue = 1000f,
                            progressColor = Color.Red,
                            sheetCollapsed = true
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        BorderProgressIndicator(
                            currentValue = 1000f,
                            maxValue = 1000f,
                            progressColor = Color.Yellow,
                            sheetCollapsed = true
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    CircularProgressIndicator(
                        currentValue = 40,
                        maxValue = 100
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalProgressIndicator(label: String, currentValue: Int, maxValue: Int, barColor: Color) {
    val clampedValue = currentValue.coerceAtMost(maxValue)

    val progress = clampedValue.toFloat() / maxValue

    Column {
        Text(text = label, color = Color.White)
        Text(text = "$clampedValue / $maxValue G", color = Color.White)
        Box(
            modifier = Modifier
                .height(18.dp)
                .width(181.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.DarkGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(barColor)
            )
        }
    }
}


@Composable
fun CircularProgressIndicator(currentValue: Int, maxValue: Int) {
    val clampedValue = currentValue.coerceAtMost(maxValue)

    val progress = clampedValue.toFloat() / maxValue
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Calories", color = Color.White)
        Text(text = "$currentValue / $maxValue kcal", color = Color.White)
        Spacer(modifier = Modifier.height(9.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(130.dp)
                .background(Color.Transparent)
        ) {
            Canvas(modifier = Modifier.size(130.dp)) {
                drawArc(
                    color = Color.DarkGray,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color.Cyan,
                    startAngle = -90f,
                    sweepAngle = 360 * progress,
                    useCenter = false,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Square)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = currentValue.toString(), color = Color.White)
            }
        }
    }
}

@Composable
private fun BorderProgressIndicator(
    currentValue: Float,
    maxValue: Float,
    progressColor: Color = Color.Cyan,
    sheetCollapsed: Boolean,
) {

    val animatedProgressValue = remember { Animatable(0f) }

    LaunchedEffect(sheetCollapsed) {
        if (sheetCollapsed) {
            animatedProgressValue.animateTo(targetValue = currentValue.coerceIn(0f, maxValue))
        }
    }

    val progress = if (maxValue > 0) {
        (animatedProgressValue.value / maxValue).coerceIn(0f, 1f)
    } else {
        0f
    }

    val pathWithProgress = remember { Path() }
    val pathMeasure = remember { PathMeasure() }
    val path = remember { Path() }

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(88.dp, 34.dp)) {
            if (path.isEmpty) {
                path.addRoundRect(
                    RoundRect(
                        Rect(offset = Offset.Zero, size),
                        cornerRadius = CornerRadius(100.dp.toPx(), 100.dp.toPx())
                    )
                )
            }
            pathWithProgress.reset()
            pathMeasure.setPath(path, forceClosed = false)

            val startDistance = pathMeasure.length * (1 - progress)
            val stopDistance = pathMeasure.length


            pathMeasure.getSegment(
                startDistance = startDistance,
                stopDistance = stopDistance,
                pathWithProgress,
                startWithMoveTo = true
            )

            clipPath(path) {
                drawRect(Color.Transparent)
            }

            drawPath(
                path = path,
                style = Stroke(2.dp.toPx()),
                color = Color.DarkGray
            )

            drawPath(
                path = pathWithProgress,
                style = Stroke(2.dp.toPx()),
                color = progressColor
            )
        }
        Text(text = "${animatedProgressValue.value.toInt()} / ${maxValue.toInt()}", color = Color.White, fontSize = 10.sp)
    }
}