package com.ilya.codewithfriends.APIclass

import android.util.Log
import com.ilya.codewithfriends.findroom.Join
import com.ilya.codewithfriends.findroom.join_room
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

class JoinDataManager {

    fun pushData_join(roomId: String, user_id: String, username: String, password: String) {
        Log.d("PushDataJoin", "Starting pushData_join method with roomId: $roomId, user_id: $user_id, username: $username, password :$password")

        // Создайте экземпляр Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создайте экземпляр службы API
        val apiService = retrofit.create(Join::class.java)

        // Создайте объект TaskRequest
        val request = join_room(roomId, user_id, username, password)

        // Отправьте POST-запрос с передачей roomId в качестве параметра пути
        val call = apiService.Join_in_room(request)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Запрос успешно отправлен
                    Log.d("PushDataJoin", "Data successfully pushed to server")
                    // Можете выполнить какие-либо дополнительные действия здесь
                } else {
                    // Обработайте ошибку, если есть
                    Log.e("PushDataJoin", "Failed to push data to server. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Обработайте ошибку при отправке запроса
                Log.e("PushDataJoin", "Failed to push data to server. Error message: ${t.message}")
            }
        })
    }

    fun post_invite( user_id: String,roomId: String, username: String, url: String) {
        Log.d("PushDataJoin", "Starting pushData_join method with roomId: $roomId, user_id: $user_id, username: $username, url :$url")

        // Создайте экземпляр Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создайте экземпляр службы API
        val apiService = retrofit.create(Post_ivite::class.java)

        // Создайте объект TaskRequest
        val request = postivite(user_id, url, username)

        // Отправьте POST-запрос с передачей roomId в качестве параметра пути
        val call = apiService.Post_invite(roomId,request)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Запрос успешно отправлен
                    Log.d("PushDataJoin", "Data successfully pushed to server")
                    // Можете выполнить какие-либо дополнительные действия здесь
                } else {
                    // Обработайте ошибку, если есть
                    Log.e("PushDataJoin", "Failed to push data to server. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Обработайте ошибку при отправке запроса
                Log.e("PushDataJoin", "Failed to push data to server. Error message: ${t.message}")
            }
        })
    }
}

interface Post_ivite {
    @POST("/invitepost/{roomid}")
    fun Post_invite(@Path("roomid") roomid: String, @Body request: postivite): Call<Void>
}


