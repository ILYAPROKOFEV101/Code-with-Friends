package com.example.codewithfriends.buttonbarnavigatino

import android.icu.text.CaseMap.Title
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Task
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.codewithfriends.buttonbarnavigatino.BottomBarScreen.Home.icon
import org.checkerframework.common.subtyping.qual.Bottom

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Home: BottomBarScreen(
         route = "Home",
        title = "Home",
        icon = Icons.Default.Home
    )
    object Chat: BottomBarScreen(
        route = "Chat",
        title = "Chat",
        icon = Icons.Default.Chat
    )
    object Task: BottomBarScreen(
        route = "Home",
        title = "Home",
        icon = Icons.Default.Task
    )
}
