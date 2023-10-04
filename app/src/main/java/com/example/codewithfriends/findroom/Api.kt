package com.example.codewithfriends.findroom

import retrofit2.Call
import retrofit2.http.GET

interface Api {
    @GET("/data")
    fun getRooms(): Call<List<Room>>
}