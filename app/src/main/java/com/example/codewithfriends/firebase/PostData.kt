package com.example.codewithfriends.firebase

import kotlinx.serialization.Serializable

data class TaskRequest(
    val gitbranch: String,
    val filename: String,
    val photo: String,
    val mession: String
)
