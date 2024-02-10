package com.ilya.codewithfriends.Viewphote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.size.Precision

class ViewPhoto : ComponentActivity() {
    companion object {
        const val PHOTO_URL_KEY = "PHOTO_URL"
    }

    @OptIn(ExperimentalCoilApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                // Получаем URL из Intent
                val photoUrl = intent.getStringExtra(PHOTO_URL_KEY)


                // Проверяем, что URL не null
                if (!photoUrl.isNullOrBlank()) {



                        var scale by remember { mutableStateOf(1f) }
                        var offset by remember { mutableStateOf(Offset.Zero) }



                                BoxWithConstraints(
                                    modifier = Modifier
                                        .fillMaxSize()

                                ) {
                                    val state = rememberTransformableState{zoomChange, panChang, rotationChange->
                                        scale = (scale * zoomChange).coerceIn(1f, 7f)
                                        val extraWidth = (scale - 1) * constraints.maxWidth
                                        val extraHeight = (scale - 1) * constraints.maxHeight

                                        val maxX = extraWidth / 2
                                        val maxY= extraHeight / 2

                                        offset = Offset(
                                            x = (offset.x + scale + panChang.x).coerceIn(-maxX, maxX),
                                            y = (offset.y + scale + panChang.y).coerceIn(-maxY, maxY)
                                        )
                                        offset += panChang
                                    }
                                    Image(
                                        painter = rememberImagePainter(
                                            data = photoUrl,
                                            builder = {
                                                precision(Precision.EXACT)
                                                // Добавьте другие параметры запроса по мере необходимости
                                            }
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer(
                                                scaleX = scale,
                                                scaleY = scale,
                                                translationX = offset.x,
                                                translationY = offset.y
                                            )
                                            .transformable(state),

                                    )
                                }

                }

            }
        }
    }
}

