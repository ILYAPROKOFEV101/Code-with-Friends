package com.ilya.codewithfriends.Viewphote

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import coil.size.Precision
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.messaging.FirebaseMessaging
import com.ilya.codewithfriends.MainViewModel
import com.ilya.codewithfriends.chattest.fragments.newUserData
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient

class ViewPhoto_fragment : Fragment() {


    private var user = mutableStateOf(emptyList<newUserData>())

    private lateinit var wiewphoto: String
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = requireContext().applicationContext,
            oneTapClient = Identity.getSignInClient(requireContext().applicationContext)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
            Log.e("Tag", "Token -> $token")
        }

        wiewphoto = arguments?.getString("VIEWPHOTO") ?: ""

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Создаем ComposeView и устанавливаем контент
        return ComposeView(requireContext()).apply {
            setContent {
                val navController = rememberNavController()

                val viewModel = viewModel<MainViewModel>()
                val isLoading by viewModel.isLoading.collectAsState()
                val swipeRefresh = rememberSwipeRefreshState(isRefreshing = isLoading)
                val id = ID(
                    userData = googleAuthUiClient.getSignedInUser()
                )
                SwipeRefresh(
                    state = swipeRefresh,
                    onRefresh = {

                    }
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()

                    ) {

                        var scale by remember { mutableStateOf(1f) }
                        var offset by remember { mutableStateOf(Offset.Zero) }



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
                                data = wiewphoto,
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