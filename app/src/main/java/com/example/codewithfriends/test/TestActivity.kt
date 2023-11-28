package com.example.codewithfriends.test

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.codewithfriends.MainActivity
import com.example.codewithfriends.buttonbarnavigatino.BottomBarScreen
import com.example.codewithfriends.chats.Chat
import com.example.codewithfriends.findroom.FindRoom
import com.example.codewithfriends.roomsetting.Roomsetting
import com.example.reaction.logik.PreferenceHelper


data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
class TestActivity () {
    private var storedRoomId: String? = null // Объявляем на уровне класса

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ButtonBar(context: Context) {
        storedRoomId = PreferenceHelper.getRoomId(context)
        Box(

            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
            ,
        ) {
            val items = listOf(

                BottomNavigationItem(
                    title = "Home",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    hasNews = false,
                ),
                BottomNavigationItem(
                    title = "Chat",
                    selectedIcon = Icons.Filled.Chat,
                    unselectedIcon = Icons.Outlined.Chat,
                    hasNews = false,
                    // badgeCount = 0
                ),
                BottomNavigationItem(
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
                modifier = Modifier.fillMaxWidth().height(80.dp).align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.background
            ) {
                NavigationBar(modifier = Modifier.align(Alignment.BottomCenter)) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                when (index) {
                                    0 -> {
                                        val intent = Intent(context, MainActivity::class.java)
                                        context.startActivity(intent)
                                    }

                                    1 -> {
                                        if(storedRoomId == ""){
                                            val intent = Intent(context, FindRoom::class.java)
                                            context.startActivity(intent)
                                        }else {
                                            val intent = Intent(context, Chat::class.java)
                                            context.startActivity(intent)
                                        }
                                    }

                                    2 -> {
                                        if(storedRoomId == ""){
                                            val intent = Intent(context, FindRoom::class.java)
                                            context.startActivity(intent)
                                        }else {
                                            val intent = Intent(context, Roomsetting::class.java)
                                            context.startActivity(intent)
                                        }
                                    }

                                    3 -> {
                                        if(storedRoomId == ""){
                                            val intent = Intent(context, FindRoom::class.java)
                                            context.startActivity(intent)
                                        }else {
                                            val intent = Intent(context, Roomsetting::class.java)
                                            context.startActivity(intent)
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
}