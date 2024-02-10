package com.ilya.codewithfriends.function

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path



interface API_FUN {
    @GET("/examination/{id}")
    fun exists(@Path("uid") roomId: String): Call<Boolean>
}