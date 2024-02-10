package com.ilya.codewithfriends.firebase

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST




import retrofit2.http.Path

interface ApiService {
    @POST("task/{roomId}")
    fun sendTaskRequest(@Path("roomId") roomId: String, @Body request: TaskRequest): Call<Void>
}
