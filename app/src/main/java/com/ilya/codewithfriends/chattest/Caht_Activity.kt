package com.ilya.codewithfriends.chattest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.ilya.codewithfriends.Startmenu.ButtonBar
import com.ilya.codewithfriends.Startmenu.FindRoom
import com.ilya.codewithfriends.Startmenu.Main_menu_fragment
import com.ilya.codewithfriends.Startmenu.Room
import com.ilya.codewithfriends.Viewphote.ViewPhoto_fragment
import com.ilya.codewithfriends.chattest.fragments.ChatFragment
import com.ilya.codewithfriends.chattest.fragments.Chatmenu
import com.ilya.codewithfriends.chattest.fragments.FreandsFragments
import com.ilya.codewithfriends.chattest.fragments.RoomChat
import com.ilya.codewithfriends.chattest.ui.theme.CodeWithFriendsTheme
import com.ilya.codewithfriends.findroom.Room
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.reaction.logik.PreferenceHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory





class Caht_Activity : Fragment(){
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = requireContext(),
            oneTapClient = Identity.getSignInClient(requireContext())
        )
    }
    private var storedRoomId: String? = null // Объявляем на уровне класса


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = UID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val img = PreferenceHelper.getimg(requireContext())
        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )


        var context = this

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
                val data_from_myroom = remember { mutableStateOf(emptyList<Room>()) }
                // Вызывайте getData только после установки ContentView
                //   GET_MYROOM("$id", data_from_myroom)
                Column(Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f) // Это позволяет Box с NavHost занять все доступное пространство, кроме выделенного для кнопок.
                            .fillMaxWidth()
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "friends",
                            modifier = Modifier.fillMaxSize()
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
                            composable("Main_Menu") {
                                Main_menu_fragment(navController)
                            }
                            composable("FindRoom") {
                                FindRoom(navController)
                            }
                            composable("Room") {
                                Room(navController)
                            }
                        }
                    }


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
fun ViewPhoto(navController: NavController,photo: String?) {
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

            val ViewPhoto_fragment = ViewPhoto_fragment().apply {
                arguments = Bundle().apply {
                    putString("VIEWPHOTO", photo)
                }
            }

            fragmentTransaction.replace(view.id, ViewPhoto_fragment)
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
