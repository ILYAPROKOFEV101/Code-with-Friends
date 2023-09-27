package com.example.codewithfriends.findroom

import kotlinx.serialization.Serializable



data class Room(
    val id: String,
    val roomName: String,
    val language: String,
    val placeInRoom: Int,
    val aboutRoom: String,
    val Admin: String,
    val url: String
)
