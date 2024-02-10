package com.ilya.codewithfriends.test

import com.ilya.codewithfriends.roomsetting.TaskResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path




interface API {
    @GET("gettask/{roomId}")
    fun getTasks(@Path("roomId") roomId: String): Call<List<TaskResponse>>
}