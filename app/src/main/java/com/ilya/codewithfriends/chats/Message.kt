package com.ilya.codewithfriends.chats

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable


@Serializable
@Immutable
data class Message(
    val img: String,
    val uid: String,
    val name: String,
    val message: String,
    val time: Long // Изменили тип на Int
)

