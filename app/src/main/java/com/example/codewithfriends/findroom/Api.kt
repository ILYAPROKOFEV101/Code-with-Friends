package com.example.codewithfriends.findroom

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("/data")
    fun getRooms(): Call<List<Room>>
}
interface Getmyroom {
    @GET("/getmyroom/{userId}")
    fun getRooms(@Path("userId") userId: String): Call<List<Room>>
}