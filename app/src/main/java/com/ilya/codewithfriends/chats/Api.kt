package com.ilya.codewithfriends.chats

import com.ilya.codewithfriends.Startmenu.new_User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {
    @GET("/exists/{roomId}/{id}/{username}")
    fun exists(@Path("roomId") roomId: String, @Path("id") id: String, @Path("username") username: String): Call<Boolean>
}
