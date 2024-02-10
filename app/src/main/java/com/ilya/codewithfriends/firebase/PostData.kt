package com.ilya.codewithfriends.firebase

data class TaskRequest(
    val gitbranch: String,
    val filename: String,
    val photo: String,
    val mession: String,
     val id: String?
)
