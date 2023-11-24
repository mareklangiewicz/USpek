package pl.mareklangiewicz.uspek.sample.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*

@Composable fun AnimatedStuff(numberTarget: Int) {
    val numberAnimated by animateIntAsState(targetValue = numberTarget, label = "some animated number")
    Row {
        val bwidth = 100.dp + numberAnimated.dp
        Box(
            Modifier
                .testTag("mybox")
                .border(2.dp, Color.Black)
                .background(Color.Green)
                .size(bwidth, 200.dp)
        ) {
            Text("w:$bwidth", Modifier.align(Alignment.Center))
        }
        Canvas(Modifier.size(100.dp, 200.dp)) {
            drawArc(Color.Blue, numberAnimated * .3f, numberAnimated * .5f, useCenter = true)
        }
    }
}

@Preview
@Composable fun AnimatedStuffPreview() {
    var numberTarget by remember { mutableStateOf(1) }
    AnimatedStuff(numberTarget)
    LaunchedEffect(Unit) {
        for (i in 1..5) {
            numberTarget = i * 100
            delay(1000)
        }
    }
}