package com.ilya.codewithfriends.findroom.FindRoom_Fragment

import android.util.Log
import com.ilya.codewithfriends.findroom.Room
import com.ilya.codewithfriends.findroom.join_room
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Get_All_rooms {
    @GET("/data/{uid}")
    fun Get_ALL_ROOMS(@Path("uid") uid: String): Call<List<Room>>
}
interface Getmyroom {
    @GET("/getmyroom/{userId}")
    fun getRooms(@Path("userId") userId: String): Call<List<Room>>
}

interface Join{
    @POST("/join")
    fun Join_in_room(@Body request: join_room): Call<Void>
}



data class join_room(
    val roomId: String,
    val user_id: String,
    val username: String,
    val password: String
)