package com.ilya.codewithfriends.roomsetting

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Path

interface Api {
    @DELETE("/deleteuser/{uid}/{roomId}")
    fun deleteRoom(@Path("uid") uid: String, @Path("roomId") roomId: String): Call<Unit>
}

interface DeleteRoom {
    @DELETE("/droproom/{roomId}/{admin}")
    fun deleteRooms( @Path("roomId") roomId: String, @Path("admin") uid: String,) : Call<Unit>
}
