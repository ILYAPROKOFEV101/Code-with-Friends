package com.example.codewithfriends.Activity.CreatyActivity

import com.example.codewithfriends.Startmenu.User
import com.example.codewithfriends.roomsetting.TaskResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Post {
    @POST("room/{uid}")
    fun Sanduser(@Path("uid") uid: String): Call<Void>

}

// Определение интерфейса Retrofit для выполнения запроса к серверу
interface ApiService {
    @GET("getnumber/{uid}")
    fun getNumber(@Path("uid") uid: String): Call<Int> // Обратите внимание, что мы используем Call<Int> здесь
}
