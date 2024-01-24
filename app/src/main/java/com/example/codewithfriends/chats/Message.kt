package com.example.codewithfriends.chats

import kotlinx.serialization.Serializable


@Serializable
data class Message(
    val img: String,
    val uid: String,
    val name: String,
    val message: String,
    val time: Long // Изменили тип на Int
)

