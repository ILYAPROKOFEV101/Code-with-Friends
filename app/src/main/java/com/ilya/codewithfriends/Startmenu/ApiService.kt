package com.ilya.codewithfriends.Startmenu
import com.ilya.codewithfriends.chattest.fragments.MyFrends
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Apiuser {
    @POST("user/{uid}")
    fun Sanduser(@Path("uid") roomId: String, @Body request: User): Call<Void>
}


interface Add_User {
    @POST("/saveFriend")
    fun Sanduser(@Body userData: new_User): Call<Void>
}

interface GET_KEY {
    @GET("/getkey/{uid}")
    fun getKey(@Path("uid") uid: String): Call<String>
}