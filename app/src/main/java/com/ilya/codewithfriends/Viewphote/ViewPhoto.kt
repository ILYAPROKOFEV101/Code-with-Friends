package com.ilya.codewithfriends.Viewphote

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.size.Precision
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util



import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ViewPhoto : ComponentActivity() {
    companion object {
        const val PHOTO_URL_KEY = "PHOTO_URL"
    }

    @OptIn(ExperimentalCoilApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val mediaUrl = intent.getStringExtra(PHOTO_URL_KEY) ?: return@setContent
            Log.d("PHOTO_URL_KEY", "$mediaUrl")
            val isVideo = isVideoUrl(mediaUrl)
            val isPhoto = isPhotoUrl(mediaUrl)

            Log.d("MEDIA_TYPE", "Is video: $isVideo, Is photo: $isPhoto")
            if(isVideo) {

                PlayVideo(mediaUrl)

            }
            else if(isPhoto) {
                DisplayImage(mediaUrl)
            }


                    // DisplayImage(mediaUrl)

                // Неизвестный тип медиа
                Log.d("MEDIA_TYPE", "Unknown media type")

        }
    }

    @Composable
    fun PlayVideo(videoUrl: String) {
        val context = LocalContext.current
        val exoPlayer = remember {
            SimpleExoPlayer.Builder(context).build().apply {
                val dataSourceFactory = DefaultDataSourceFactory(
                    context,
                    Util.getUserAgent(context, context.packageName)
                )
                val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(videoUrl))
                setMediaSource(videoSource)
                prepare()
            }
        }

        AndroidView(
            factory = { ctx ->
                StyledPlayerView(ctx).apply {
                    player = exoPlayer
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )


        // Release the player when activity is destroyed
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                exoPlayer.release()
            }
        })
    }



    @Composable
    fun DisplayImage(imageUrl: String) {
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
                    data = imageUrl,
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

fun isVideoUrl(url: String): Boolean {
    return url.contains("https://firebasestorage.googleapis.com/v0/b/code-with-friends-73cde.appspot.com/o/video", ignoreCase = true)
}

fun isPhotoUrl(url: String): Boolean {
    return url.contains("https://firebasestorage.googleapis.com/v0/b/code-with-friends-73cde.appspot.com/o/image", ignoreCase = true)
}