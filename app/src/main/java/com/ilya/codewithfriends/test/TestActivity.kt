package com.ilya.codewithfriends.test

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ilya.codewithfriends.MainActivity
import com.ilya.codewithfriends.Startmenu.Main_menu
import com.ilya.codewithfriends.chats.Chat
import com.ilya.codewithfriends.chattest.Caht_Activity
import com.ilya.codewithfriends.findroom.FindRoom
import com.ilya.codewithfriends.roomsetting.Roomsetting
import com.ilya.reaction.logik.PreferenceHelper


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
                .background(Color.White.copy(alpha = 0.5f))
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
                                        val intent = Intent(context, Main_menu::class.java)
                                        context.startActivity(intent)
                                      //  (context as? Activity)?.finish() // Закрываем текущую активити
                                    //    (context as? Activity)?.finish() // Закрываем текущую активити
                                    }

                                    1 -> {
                                            val intent = Intent(context, Caht_Activity::class.java)
                                            if (context !is MainActivity) {
                                                context.startActivity(intent)
                                             //   (context as? Activity)?.finish() // Закрываем текущую активити, если это не MainActivity
                                            } else {
                                                context.startActivity(intent)
                                            }
                                    }

                                    2 -> {
                                        if(storedRoomId?.isNullOrEmpty() == false){
                                            val intent = Intent(context, Roomsetting::class.java)
                                            if (context !is MainActivity) {
                                                context.startActivity(intent)
                                                //   (context as? Activity)?.finish() // Закрываем текущую активити, если это не MainActivity
                                            } else {
                                                context.startActivity(intent)
                                            }

                                        }else {

                                            val intent = Intent(context, FindRoom::class.java)
                                            context.startActivity(intent)
                                            (context as? Activity)?.finish() // Закрываем текущую активити

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