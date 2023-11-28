package com.example.codewithfriends.roomsetting

import com.example.codewithfriends.findroom.Room
import com.example.codewithfriends.firebase.TaskRequest
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService_ilya {
    @GET("gettask/{roomId}")
    fun getTasks(@Path("roomId") roomId: String): Call<List<TaskResponse>>
}
interface OVER_DELETE{
    @GET("getinfo/{roomId}")
    fun OVER_AND_DELETE(@Path("roomId") roomId: String): Call<List<Over_DeletetItem>>
}


interface Apidelte {
    @DELETE("delete/{roomId}/{id}/plus")
    suspend fun delete(@Path("roomId") roomId: String, @Path("id") postId: String): Response<Unit>
}
interface API_DELET {
    @DELETE("delete/{roomId}/{id}/minus")
    suspend fun delete(@Path("roomId") roomId: String, @Path("id") postId: String): Response<Unit>
}

interface Kick {
    @DELETE("kick/{roomId}/{userId}")
    fun user(@Path("roomId") roomId: String, @Path("userId") userId: String): Call<Unit>
}




interface Addids{
    @POST("/ids")
    fun Sanduser(@Body request: ids): Call<Void>
}

interface GetmyAPI {
    @GET("/getimg/{roomId}")
    fun GETIMG(@Path("roomId") roomId: String): Call<List<Room2>>
}
