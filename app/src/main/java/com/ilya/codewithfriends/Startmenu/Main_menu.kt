package com.ilya.codewithfriends.Startmenu

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*


import android.view.View
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Task
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.ilya.codewithfriends.Startmenu.Menu_Fragment.Mainmenufragment

import com.ilya.codewithfriends.chattest.ChatRoomm
import com.ilya.codewithfriends.chattest.ChatScreen
import com.ilya.codewithfriends.chattest.ChatmenuContent


import com.ilya.codewithfriends.chattest.fragments.FreandsFragments
import com.ilya.codewithfriends.findroom.FindRoom_Fragment.FindRoom_fragment
import com.ilya.codewithfriends.roomsetting.Room_Fragments.Room_fragment
import com.ilya.reaction.logik.PreferenceHelper.getRoomId

interface FragmentManagerProvider_manu {
    fun provideFragmentManager(): FragmentManager
}



class Main_menu : FragmentActivity(), FragmentManagerProvider_manu {


    private var showit by mutableStateOf(true)
    override fun provideFragmentManager(): FragmentManager {
        return supportFragmentManager
    }

//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var context = this

    val room = intent.getStringExtra("Room")



        setContent {
            val navController = rememberNavController()



            Column(Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (room != null) {
                            "Room"
                        } else {
                            "Main_Menu"
                        }

                    ) {
                        composable("Main_Menu") {
                            Main_menu_fragment()
                        }
                        composable("FindRoom") {
                            FindRoom()
                        }
                        composable("friends") {
                            Friends()
                        }
                        composable("Room") {
                            Room()
                        }
                    }
                }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        ButtonAppBar(navController,  context)
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

    fun clousAppbar(show: Boolean){
        showit = show
    }
}

@Composable
fun Main_menu_fragment() {
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
fun Friends() {

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
            val friendsFragment = FreandsFragments()
            fragmentTransaction.replace(view.id, friendsFragment)
            fragmentTransaction.commit()
        }
    )
}



@Composable
fun Room() {
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
fun FindRoom() {
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
fun ButtonAppBar(navController: NavController, context: Context) {

    Box(
        modifier = Modifier
            .fillMaxSize()
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
                                    navController.navigate("friends")
                                }

                                3 -> {
                                     if(getRoomId(context) == null){
                                         navController.navigate("FindRoom")
                                    } else  {
                                         navController.navigate("Room")
                                    }


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

@Composable
fun MyNavHost(showFragment: String) {
    var navController = rememberNavController()
    Column(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {


    NavHost(
        navController = navController,
        startDestination = "$showFragment"
    ) {


        composable("Main_Menu") {
            Main_menu_fragment()
        }
        composable("FindRoom") {
            FindRoom()
        }
        composable("friends") {
            Friends()
        }
        composable("Room") {
            Room()
        }
        composable("chatmenu") {
            // Ваш фрагмент Chatmenu

        }


    /*
        composable("chat/{roomId}") { backStackEntry ->
            // Получаем roomId из аргументов, если он не передан, используем значение по умолчанию
            val roomId = backStackEntry.arguments?.getString("roomId") ?: roomId
            // Ваш фрагмент ChatScreen с передачей roomId
            ChatScreen(navController, roomId ?: "")
        }
        composable("RoomChat/{socketId}") { backStackEntry ->
            // Получаем socketId из аргументов, если он не передан, используем значение по умолчанию
            val socketId = backStackEntry.arguments?.getString("socketId") ?: socketId
            // Ваш фрагмент ChatRoomm с передачей socketId
            ChatRoomm(navController, socketId ?: "")
        }*/
    }
        }

    }
}