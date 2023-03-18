package com.study.localmusic.ui.widget

import android.graphics.Bitmap
import android.util.TypedValue
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.study.localmusic.R
import com.study.localmusic.util.formatTime

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 10:17 PM
 */
@Composable
fun PlayControl(
    name: String,
    playDuration: Int,
    bitmap: Bitmap,
    isPlay: Boolean,
    playAction: () -> Unit
) {
    val pauseIcon = R.drawable.icon_play
    val playIcon = R.drawable.icon_pause
    val infiniteTransition = rememberInfiniteTransition()
    val angle = if (isPlay) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(animation = tween(10000, easing = LinearEasing))
        ).value
    } else 0f

    Row(
        Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(color = Color.Black)
            .padding(5.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = bitmap,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .graphicsLayer { rotationZ = angle },
            contentDescription = null
        )

        Column(
            Modifier
                .weight(1f)
                .padding(start = 10.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = name,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(start = 5.dp)
            )
        }
        Box(Modifier.size(60.dp), contentAlignment = Alignment.Center) {

            Image(
                painter = painterResource(id = if (isPlay) playIcon else pauseIcon),
                modifier = Modifier
                    .clickable {
                        playAction()
                    }
                    .size(40.dp),
                contentDescription = null
            )
        }
    }
}