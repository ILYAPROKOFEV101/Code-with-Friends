package com.example.codewithfriends.roomsetting

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @DELETE("/deleteuser/{uid}/{roomId}")
    fun deleteRoom(@Path("uid") uid: String, @Path("roomId") roomId: String): Call<Unit>
}
