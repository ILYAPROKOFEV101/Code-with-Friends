package com.ilya.codewithfriends.Startmenu.Menu_Fragment
import android.content.Context
import com.ilya.codewithfriends.Startmenu.Change_DC
import com.ilya.codewithfriends.Startmenu.Change_DC_Name
import com.ilya.codewithfriends.Startmenu.User
import com.ilya.codewithfriends.Startmenu.new_User
import com.ilya.codewithfriends.chattest.fragments.MyFrends
import com.ilya.reaction.logik.PreferenceHelper
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.io.IOException

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

fun getKeyFromServer(uid: String, context: Context, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit) {
    val client = OkHttpClient()
    val url = "https://getpost-ilya1.up.railway.app/getkey/$uid"

    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: okhttp3.Call, response: Response) {
            val body = response.body?.string()
            if (response.isSuccessful && body != null) {
                // Сохраняем ключ в SharedPreferences
                PreferenceHelper.saveDataToSharedPreferences(context, "my_key", body)

                onSuccess(body)
                println("Received key: $body")
            } else {
                val error = IOException("Failed to get key. Error code: ${response.code}")
                onError(error)
                println("Failed to get key: ${error.message}")
            }
        }

        override fun onFailure(call: okhttp3.Call, e: IOException) {
            onError(e)
            println("Request failed: ${e.message}")
        }
    })
}
