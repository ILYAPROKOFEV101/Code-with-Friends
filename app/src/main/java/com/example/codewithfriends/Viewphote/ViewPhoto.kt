package com.example.codewithfriends.Viewphote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.size.Precision
import com.example.codewithfriends.Viewphote.ui.theme.CodeWithFriendsTheme
import com.example.codewithfriends.roomsetting.TaskData

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

