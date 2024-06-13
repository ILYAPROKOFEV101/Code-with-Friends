import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.ilya.codewithfriends.APIclass.JoinDataManager
import com.ilya.codewithfriends.MainViewModel
import com.ilya.codewithfriends.R
import com.ilya.codewithfriends.Startmenu.Room
import com.ilya.codewithfriends.Viewphote.ViewPhoto
import com.ilya.codewithfriends.chats.Message
import com.ilya.codewithfriends.createamspeck.ui.theme.CodeWithFriendsTheme
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.reaction.logik.PreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.java_websocket.client.WebSocketClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

class FriendsChatFragment : Fragment() {


    private lateinit var video: String
    /*var video: String? = null
        set(value) {
            field = value
            updateVideo()
        }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        video = arguments?.getString("VIDEO_URL") ?: ""
        return ComposeView(requireContext()).apply {
            setContent {
                ShowVideoplayer(video ?: "")
                Log.d("video_url", "$video")
            }
        }
    }



    companion object {
        private const val ARG_VIDEO = "video"

        fun newInstance(video: String): FriendsChatFragment {
            val fragment = FriendsChatFragment()
            val args = Bundle().apply {
                putString(ARG_VIDEO, video)
            }
            fragment.arguments = args
            return fragment
        }
    }
}



    @Composable
    fun ShowVideoplayer(
        videoUrl: String,
        modifier: Modifier = Modifier,
        onVideoCompleted: () -> Unit = {}
    ) {
        var exoPlayer: ExoPlayer? by remember { mutableStateOf(null) }
        var isPlayerReady by remember { mutableStateOf(true) }
        var previewImage: Bitmap? by remember { mutableStateOf(null) }
        val context = LocalContext.current

        val currentPosition = remember { mutableStateOf(0L) }
        val duration = remember { mutableStateOf(0L) }
        val videoStarted = remember { mutableStateOf(false) }
        val lifecycleOwner = LocalLifecycleOwner.current

        DisposableEffect(key1 = exoPlayer) {
            val interval = 1000L // Update every second
            val runnable = Runnable {
                currentPosition.value = exoPlayer!!.currentPosition
                duration.value = exoPlayer!!.duration
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

        Column(modifier = modifier.fillMaxSize()) {
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
                            .padding(bottom = 20.dp)
                            .clickable {
                                if (it.isPlaying) {
                                    it.pause()
                                } else {
                                    it.play()
                                }
                            }
                    )
                if (duration.value > 0L) {
                    Box(modifier = Modifier.fillMaxWidth().height(20.dp)) {
                        Slider(
                            value = currentPosition.value.toFloat(),
                            onValueChange = {
                                exoPlayer?.seekTo(it.toLong())
                                currentPosition.value = it.toLong()
                            },
                            valueRange = 0f..duration.value.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(0.5f),
                         //   steps = 1000
                            colors = SliderDefaults.colors(
                                Color(0xFF6385FF),
                            )
                        )
                    }
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