package com.ilya.codewithfriends.roomsetting

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

interface Api {
    @DELETE("/deleteuser/{uid}/{roomId}")
    fun deleteRoom(@Path("uid") uid: String, @Path("roomId") roomId: String): Call<Unit>
}

interface DeleteRoom {
    @DELETE("/droproom/{roomId}/{admin}")
    fun deleteRooms( @Path("roomId") roomId: String, @Path("admin") uid: String,) : Call<Unit>
}

interface Add_user_Invite {
    @PUT("/invite/{uid}/{id}/{user}/{roomid}")
    fun putUSER(
        @Path("uid") uid: String,
        @Path("id") id: String,
        @Path("user") user: String,
        @Path("roomid") roomId: String
    ): Call<Void> // Define return type as Call<Void> since it's a PUT request
}