package com.ilya.codewithfriends.chattest

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.ilya.codewithfriends.chattest.fragments.ChatFragment
import com.ilya.codewithfriends.chattest.fragments.Chatmenu
import com.ilya.codewithfriends.chattest.fragments.FreandsFragments
import com.ilya.codewithfriends.chattest.fragments.RoomChat
import com.ilya.codewithfriends.chattest.ui.theme.CodeWithFriendsTheme
import com.ilya.codewithfriends.findroom.Getmyroom
import com.ilya.codewithfriends.findroom.Room
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


interface FragmentManagerProvider {
    fun provideFragmentManager(): FragmentManager
}


class Caht_Activity : FragmentActivity(), FragmentManagerProvider {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private var storedRoomId: String? = null // Объявляем на уровне класса


    override fun provideFragmentManager(): FragmentManager {
        return supportFragmentManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = UID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val img = IMG(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )



        setContent {

            val navController = rememberNavController()
            val data_from_myroom = remember { mutableStateOf(emptyList<Room>()) }
            // Вызывайте getData только после установки ContentView
         //   GET_MYROOM("$id", data_from_myroom)
            NavHost(
                navController = navController,
                startDestination = "friends"
            ) {
                composable("friends") {
                    Freands(navController)
                }
                composable("chatmenu") {
                    ChatmenuContent(navController)
                }
                composable("chat") {
                    ChatScreen(navController, "roomId")
                }
                composable("RoomChat") {
                    ChatRoomm(navController, "roomId")
                }
            }

        }
    }





}

@Composable
fun ChatScreen(navController: NavController, storedRoomId: String?) {
    AndroidView(
        factory = { context ->
            FragmentContainerView(context).apply {
                id = View.generateViewId()
            }
        },
        update = { view ->

            val fragmentManager = (view.context as FragmentActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val chatFragment = ChatFragment().apply {
                arguments = Bundle().apply {
                    putString("STORED_ROOM_ID_KEY", storedRoomId)
                }
            }

            fragmentTransaction.replace(view.id, chatFragment)
            fragmentTransaction.commit()

        }
    )


}

@Composable
fun ChatmenuContent(navController: NavController) {
    AndroidView(
        factory = { context ->
            // Создаем FragmentContainerView
            FragmentContainerView(context).apply {
                id = View.generateViewId()
            }
        },
        update = { view ->
            // Получаем FragmentManager
            val fragmentManager = (view.context as FragmentActivity).supportFragmentManager
            // Создаем и добавляем Chatmenu фрагмент
            val fragmentTransaction = fragmentManager.beginTransaction()
            val chatmenuFragment = Chatmenu()
            fragmentTransaction.replace(view.id, chatmenuFragment)
            fragmentTransaction.commit()
        }
    )


}
@Composable
fun Freands(navController: NavController) {
    AndroidView(
        factory = { context ->
            // Создаем FragmentContainerView
            FragmentContainerView(context).apply {
                id = View.generateViewId()
            }
        },
        update = { view ->
            // Получаем FragmentManager
            val fragmentManager = (view.context as FragmentActivity).supportFragmentManager
            // Создаем и добавляем Chatmenu фрагмент
            val fragmentTransaction = fragmentManager.beginTransaction()
            val chatmenuFragment = FreandsFragments()
            fragmentTransaction.replace(view.id, chatmenuFragment)
            fragmentTransaction.commit()
        }
    )


}

@Composable
fun ChatRoomm(navController: NavController, soket: String?) {
    AndroidView(
        factory = { context ->
            // Создаем FragmentContainerView
            FragmentContainerView(context).apply {
                id = View.generateViewId()
            }
        },
        update = { view ->
            // Получаем FragmentManager
            val fragmentManager = (view.context as FragmentActivity).supportFragmentManager
            // Создаем и добавляем Chatmenu фрагмент
            val fragmentTransaction = fragmentManager.beginTransaction()

            val chatmenuFragment = RoomChat().apply {
                arguments = Bundle().apply {
                    putString("ROOM_KEY", soket)
                }
            }

            fragmentTransaction.replace(view.id, chatmenuFragment)
            fragmentTransaction.commit()
        }
    )
}
