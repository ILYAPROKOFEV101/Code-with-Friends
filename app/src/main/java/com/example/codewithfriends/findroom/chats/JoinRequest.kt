package com.example.codewithfriends.findroom.chats

import kotlinx.serialization.Serializable

@Serializable
data class JoinRequest(
    val roomId: String,
    val user_id: String,
    val username: String,
    val image_url: String
)
