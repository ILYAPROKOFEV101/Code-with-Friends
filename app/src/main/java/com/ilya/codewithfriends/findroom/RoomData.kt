package com.ilya.codewithfriends.findroom


data class Room(
    val id: String,
    val roomName: String,
    val language: String,
    val placeInRoom: Int,
    val aboutRoom: String,
    val Admin: String,
    val url: String,
    val hasPassword: Boolean
)

data class join_room(
    val roomId: String,
    val user_id: String,
    val username: String,
    val password: String
)