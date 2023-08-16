package com.example.codewithfriends.datas

import com.google.firebase.database.Exclude

data class User(
    @Exclude var id: String? = null,
    var profilePictureUrl: String? = null,
    var username: String? = null,
    var result: Int? = null
)