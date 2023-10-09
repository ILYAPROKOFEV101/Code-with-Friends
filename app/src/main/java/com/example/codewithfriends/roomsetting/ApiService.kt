package com.example.codewithfriends.roomsetting

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("gettask/{roomId}")
    fun getTasks(@Path("roomId") roomId: String): Call<List<TaskResponse>>
}


interface Apidelte {
    @DELETE("delete/{roomId}/{id}/plus")
    suspend fun delete(@Path("roomId") roomId: String, @Path("id") postId: String): Response<Unit>
}