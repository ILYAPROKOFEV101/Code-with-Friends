package com.ilya.codewithfriends.Startmenu

import LoadingComponent
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState


import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilya.codewithfriends.R
import com.ilya.codewithfriends.Activity.CreatyActivity.ApiService
import com.ilya.codewithfriends.Activity.CreatyActivity.CreativyRoom
import com.ilya.codewithfriends.MainViewModel

import com.ilya.codewithfriends.findroom.FindRoom
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG

import com.ilya.codewithfriends.presentation.profile.ProfileIcon
import com.ilya.codewithfriends.presentation.profile.ProfileName
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.codewithfriends.test.TestActivity
import com.ilya.reaction.logik.PreferenceHelper
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.auth.api.identity.Identity
import com.ilya.codewithfriends.chattest.Caht_Activity
import com.ilya.reaction.logik.PreferenceHelper.getDataFromSharedPreferences
import getKeyFromServer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import android.app.NotificationManager

import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.icu.number.Scale
import android.media.AudioManager
import android.media.audiofx.BassBoost
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Task
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.ilya.codewithfriends.MainActivity
import com.ilya.codewithfriends.Startmenu.Menu_Fragment.Mainmenufragment

import com.ilya.codewithfriends.chattest.ChatRoomm
import com.ilya.codewithfriends.chattest.ChatScreen
import com.ilya.codewithfriends.chattest.ChatmenuContent

import com.ilya.codewithfriends.chattest.Freands
import com.ilya.codewithfriends.chattest.fragments.Chatmenu
import com.ilya.codewithfriends.chattest.fragments.FreandsFragments
import com.ilya.codewithfriends.chattest.fragments.RoomChat
import com.ilya.codewithfriends.findroom.FindRoom_Fragment.FindRoom_fragment
import com.ilya.codewithfriends.findroom.Room
import com.ilya.codewithfriends.presentation.sign_in.UserData
import com.ilya.codewithfriends.roomsetting.Room_Fragments.Room_fragment
import com.ilya.codewithfriends.roomsetting.Roomsetting
import com.ilya.reaction.logik.PreferenceHelper.saveimg
import com.ilya.reaction.logik.PreferenceHelper.savename
import java.util.UUID

interface FragmentManagerProvider_manu {
    fun provideFragmentManager(): FragmentManager
}


class Main_menu : FragmentActivity(), FragmentManagerProvider_manu {


    override fun provideFragmentManager(): FragmentManager {
        return supportFragmentManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var context = this


        setContent {
            val navController = rememberNavController()

            var Show_bar by remember {
                mutableStateOf(true)
            }
            Column(Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f) // Это позволяет Box с NavHost занять все доступное пространство, кроме выделенного для кнопок.
                        .fillMaxWidth()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "Main_Menu",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("Main_Menu") {
                            Main_menu_fragment(navController)
                        }
                        composable("FindRoom") {
                            FindRoom(navController)
                        }
                        composable("Chat") {
                            Chat(navController)

                        }
                        composable("Room") {
                            Room(navController)

                        }
                    }
                }
                if(Show_bar) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .align(Alignment.CenterHorizontally) // Центрируем по горизонтали, если вдруг Box не будет на всю ширину
                    ) {
                        ButtonBar(navController, context)
                    }
                }
            }
        }



    }


    data class BottomNavigationItem(
        val title: String,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
        val hasNews: Boolean,
        val badgeCount: Int? = null
    )





}

@Composable
fun Main_menu_fragment(navController: NavController) {
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
            val FindRoom_fragment = Mainmenufragment()
            fragmentTransaction.replace(view.id, FindRoom_fragment)
            fragmentTransaction.commit()
        }
    )
}
@Composable
fun Chat(navController: NavController) {
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
            val FindRoom_fragment = Caht_Activity()
            fragmentTransaction.replace(view.id, FindRoom_fragment)
            fragmentTransaction.commit()
        }
    )
}





@Composable
fun Room(navController: NavController) {
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
            val FindRoom_fragment = Room_fragment()
            fragmentTransaction.replace(view.id, FindRoom_fragment)
            fragmentTransaction.commit()
        }
    )


}

@Composable
fun FindRoom(navController: NavController) {
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
            val FindRoom_fragment = FindRoom_fragment()
            fragmentTransaction.replace(view.id, FindRoom_fragment())
            fragmentTransaction.commit()
        }
    )


}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonBar(navController: NavController, context: Context) {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White.copy(alpha = 0.5f)),
    ) {
        val items = listOf(

            Main_menu.BottomNavigationItem(
                title = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                hasNews = false,
            ),
            Main_menu.BottomNavigationItem(
                title = "Rooms",
                selectedIcon = Icons.Filled.MeetingRoom,
                unselectedIcon = Icons.Outlined.MeetingRoom,
                hasNews = false,
            ),
            Main_menu.BottomNavigationItem(
                title = "Chat",
                selectedIcon = Icons.Filled.Chat,
                unselectedIcon = Icons.Outlined.Chat,
                hasNews = false,
                // badgeCount = 0
            ),
            Main_menu.BottomNavigationItem(
                title = "Task",
                selectedIcon = Icons.Filled.Task,
                unselectedIcon = Icons.Outlined.Task,
                hasNews = true,
            ),
        )
        var selectedItemIndex by rememberSaveable {
            mutableStateOf(0)
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)) // Установите прозрачность для Surface
        ) {
            NavigationBar(modifier = Modifier.align(Alignment.BottomCenter)) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            when (index) {
                                0 -> {
                                    navController.navigate("Main_Menu")
                                }
                                1 -> {
                                    navController.navigate("FindRoom")
                                }

                                2 -> {
                                    navController.navigate("Chat")
                                }

                                3 -> {
                                    navController.navigate("Room")
                                }

                            }
                        },
                        label = {
                            Text(text = item.title)
                        },
                        alwaysShowLabel = false,
                        icon = {
                            BadgedBox(
                                modifier = Modifier.align(Alignment.Bottom),
                                badge = {
                                    if (item.badgeCount != null) {
                                        Badge {
                                            Text(text = item.badgeCount.toString())
                                        }
                                    } else if (item.hasNews) {
                                        Badge()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (index == selectedItemIndex) {
                                        item.selectedIcon
                                    } else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
