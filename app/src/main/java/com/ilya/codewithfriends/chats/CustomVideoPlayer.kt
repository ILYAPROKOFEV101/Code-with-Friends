package com.ilya.codewithfriends.chats

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.os.Handler
import android.os.Looper
import android.transition.Transition
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import coil.compose.rememberImagePainter
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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.google.firebase.storage.StorageReference


@Composable
fun CustomVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    onVideoCompleted: () -> Unit = {}
) {
    var exoPlayer: ExoPlayer? by remember { mutableStateOf(null) }
    var isPlayerReady by remember { mutableStateOf(false) }
    var previewImage: Bitmap? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    // Загрузка превью
    LaunchedEffect(videoUrl) {
        Glide.with(context)
            .asBitmap()
            .load(videoUrl)
            .frame(1) // Захватить кадр на 1-й секунде
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    previewImage = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })



    }


    DisposableEffect(Unit) {
        val exoPlayerInstance = SimpleExoPlayer.Builder(context).build().apply {
            val dataSourceFactory = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, context.packageName)
            )
            val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUrl))
            setMediaSource(videoSource)
            prepare()
            playWhenReady = false // Начать с паузы
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == ExoPlayer.STATE_READY && !isPlayerReady) {
                        isPlayerReady = true
                        pause() // Удерживаем первый кадр
                    }
                    if (state == ExoPlayer.STATE_ENDED) {
                        onVideoCompleted()
                    }
                }
            })
        }
        exoPlayer = exoPlayerInstance

        onDispose {
            exoPlayerInstance.release()
        }
    }

    Column(modifier = modifier.fillMaxWidth(0.5f).clip(RoundedCornerShape(20.dp))) {
        if (previewImage != null && !isPlayerReady) {
            Box(modifier = Modifier.fillMaxSize()) {

                    val compressedImage = compressBitmap(previewImage!!, 50) // Качество 50 из 100
                    Image(bitmap = compressedImage.asImageBitmap(), contentDescription = "Video Preview",
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)))

            }
        } else {
            exoPlayer?.let {
                AndroidView(
                    factory = { ctx ->
                        StyledPlayerView(ctx).apply {
                            player = it
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (it.isPlaying) {
                                it.pause()
                            } else {
                                it.play()
                            }
                        }
                )
            }
        }
    }
}


fun compressBitmap(source: Bitmap, quality: Int): Bitmap {
    val outputStream = ByteArrayOutputStream()
    source.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    val byteArray = outputStream.toByteArray()
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

/*

 val currentPosition = remember { mutableStateOf(0L) }
    val duration = remember { mutableStateOf(0L) }
    val videoStarted = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

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

if (duration.value > 0L) {
    Box(modifier = Modifier.fillMaxWidth().height(5.dp)) {
        Slider(
            value = currentPosition.value.toFloat(),
            onValueChange = {
                exoPlayer?.seekTo(it.toLong())
                currentPosition.value = it.toLong()
            },
            valueRange = 0f..duration.value.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0.1f),
            steps = 100
        )
    }


}*/
