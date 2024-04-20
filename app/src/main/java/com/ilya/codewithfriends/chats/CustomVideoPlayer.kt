package com.ilya.codewithfriends.chats

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


@Composable
fun CustomVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    onVideoCompleted: () -> Unit = {}
) {


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
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == ExoPlayer.STATE_ENDED) {
                        onVideoCompleted()
                    }
                }
            })
        }
    }

    val currentPosition = remember { mutableStateOf(0L) }
    val duration = remember { mutableStateOf(0L) }

    val previewImageBitmap = remember { mutableStateOf<ImageBitmap?>(null) }
    val videoStarted = remember { mutableStateOf(false) }

    DisposableEffect(key1 = exoPlayer) {
        val interval = 1000L // Update every second
        val runnable = Runnable {
            currentPosition.value = exoPlayer.currentPosition
            duration.value = exoPlayer.duration
        }
        val handler = Handler(Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                runnable.run()
                handler.postDelayed(this, interval)
            }
        }
        handler.post(updateRunnable)
        onDispose {
            handler.removeCallbacks(updateRunnable)
        }
    }

    LaunchedEffect(videoUrl) {
        val bitmap = getFirstFrameBitmap(videoUrl, 50) // Пример уровня качества: 50
        previewImageBitmap.value = bitmap?.asImageBitmap()
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (previewImageBitmap.value != null && !videoStarted.value) {
            Image(
                bitmap = previewImageBitmap.value!!,
                contentDescription = null,
                modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                    .clickable {
                        videoStarted.value = true
                        exoPlayer.play()
                    }
            )
        } else {
            AndroidView(
                factory = { ctx ->
                    StyledPlayerView(ctx).apply {
                        player = exoPlayer
                        controllerAutoShow = true
                        controllerHideOnTouch = true
                        useController = false
                        setShowNextButton(false)
                        setShowPreviousButton(false)
                        setShowFastForwardButton(false)
                        setShowRewindButton(false)
                        setShowShuffleButton(false)
                    }
                },
                modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
                    .clickable {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.play()
                        }
                    }
            )

            if (duration.value > 0L) {
                Box(modifier = Modifier.fillMaxWidth().height(5.dp)) {
                    Slider(
                        value = currentPosition.value.toFloat(),
                        onValueChange = {
                            exoPlayer.seekTo(it.toLong())
                            currentPosition.value = it.toLong()
                        },
                        valueRange = 0f..duration.value.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(1f),
                        steps = 100 // Ограничим количество шагов слайдера
                    )
                }
            }

            if (duration.value > 0L) {
                Slider(
                    value = currentPosition.value.toFloat(),
                    onValueChange = {
                        exoPlayer.seekTo(it.toLong())
                        currentPosition.value = it.toLong()
                    },
                    valueRange = 0f..duration.value.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    steps = 100 // Ограничим количество шагов слайдера
                )
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = exoPlayer) {
        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                exoPlayer.release()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


private fun getFirstFrameBitmap(videoUrl: String, quality: Int): Bitmap? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(videoUrl)
    val data = retriever.frameAtTime
    retriever.release()

    val outputStream = ByteArrayOutputStream()
    // Используйте параметр quality для управления качеством изображения
    data?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    val compressedData = outputStream.toByteArray()
    return BitmapFactory.decodeByteArray(compressedData, 0, compressedData.size)
}