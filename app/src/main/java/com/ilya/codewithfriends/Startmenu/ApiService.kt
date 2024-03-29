package com.ilya.codewithfriends.Startmenu
import com.ilya.codewithfriends.chattest.fragments.MyFrends
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Apiuser {
    @POST("postuser/{uid}")
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

interface ApiService {
    // POST запрос для изменения URL
    @POST("/change/{uid}")
    fun changeUrl(@Path("uid") uid: String, @Body changeDC: Change_DC): Call<Void>

    // POST запрос для изменения имени пользователя
    @POST("/changeName/{uid}")
    fun changeName(@Path("uid") uid: String, @Body changeDCName: Change_DC_Name): Call<Void>
}

interface ChangeUserNameService {
    @POST("/change_name/{uid}")
    fun changeUserName(
        @Path("uid") uid: String,
        @Body changeDCName: Change_DC_Name
    ): Call<Void>
}
interface ChangeUserURLService {
    @POST("/change/{uid}")
    fun changeUserURL(
        @Path("uid") uid: String,
        @Body changeDCURL: Change_DC
    ): Call<Void>
}
