package com.ilya.codewithfriends.chattest

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.google.android.gms.auth.api.identity.Identity
import com.ilya.codewithfriends.chattest.fragments.ChatFragment
import com.ilya.codewithfriends.chattest.fragments.Chatmenu
import com.ilya.codewithfriends.chattest.fragments.FreandsFragments
import com.ilya.codewithfriends.chattest.ui.theme.CodeWithFriendsTheme
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient


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
            CodeWithFriendsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val fragmentManager = remember { supportFragmentManager }
                        //    ChatScreen(fragmentManager, "bgbruge")
                        //  Log.d("storedRoomId","$storedRoomId")
                   // ChatmenuContent() // Вызываем ChatmenuContent
                    Freands()
                }
            }
        }
    }




}

@Composable
fun ChatScreen(storedRoomId: String?) {
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
fun ChatmenuContent() {
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
fun Freands() {
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