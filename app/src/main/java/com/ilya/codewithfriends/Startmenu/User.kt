package com.ilya.codewithfriends.Startmenu

import android.util.Log
import com.google.gson.Gson
import com.ilya.codewithfriends.roomsetting.Addids
import com.ilya.codewithfriends.roomsetting.ids
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class User(
    val aboutme: String,
    val age: Int,
    val uid: String,
    val url: String
)


data class new_User(
    val username: String,
    val user_id: String,
    val image_url: String
)

data class Change_DC(
    val url: String?
)
data class Change_DC_Name(
    val name: String?
)


fun Adduser(username: String, user_id: String, image_url: String) {
    Log.d("Adduser", "Starting Adduser request with username: $username, user_id: $user_id, image_url: $image_url")

    // Создайте экземпляр Retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("https://getpost-ilya1.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Создайте экземпляр службы API
    val apiService = retrofit.create(Add_User::class.java)

    // Создайте объект ids с вашими данными
    val userData = new_User(username, user_id, image_url)

    // Отправьте POST-запрос
    val call = apiService.Sanduser(userData)
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                // Запрос успешно отправлен
                Log.d("Adduser", "Adduser request successful")
                // Можете выполнить какие-либо дополнительные действия здесь
            } else {
                // Обработайте ошибку, если есть
                Log.e("Adduser", "Adduser request failed with code: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            // Обработайте ошибку при отправке запроса
            Log.e("Adduser", "Adduser request failed", t)
        }
    })
}

// Функция для отправки POST-запроса на изменение URL
fun changeUrl(baseUrl: String, uid: String, newUrl: String, callback: (Boolean) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)
    val changeDC = Change_DC(newUrl)
    val call = apiService.changeUrl(uid, changeDC)

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            callback(response.isSuccessful)
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            callback(false)
        }
    })
}

// Функция для отправки POST-запроса на изменение имени пользователя
fun changeUserName(uid: String, newName: String) {
    Log.d("ChangeUserName", "Starting ChangeUserName request with uid: $uid, newName: $newName")

    // Создайте экземпляр Retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("https://getpost-ilya1.up.railway.app")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Создайте экземпляр службы API
    val apiService = retrofit.create(ChangeUserNameService::class.java)

    // Создайте объект с новым именем пользователя
    val changeDCName = Change_DC_Name(newName)

    // Отправьте POST-запрос
    val call = apiService.changeUserName(uid, changeDCName)
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                // Запрос успешно отправлен
                Log.d("ChangeUserName", "ChangeUserName request successful")
                // Можете выполнить какие-либо дополнительные действия здесь
            } else {
                // Обработайте ошибку, если есть
                Log.e("ChangeUserName", "ChangeUserName request failed with code: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            // Обработайте ошибку при отправке запроса
            Log.e("ChangeUserName", "ChangeUserName request failed", t)
        }
    })
}



// Функция для отправки POST-запроса на изменение имени пользователя
fun changeUserURL(uid: String, URL: String) {
    Log.d("ChangeUserName", "Starting ChangeUserName request with uid: $uid, newName: $URL")

    // Создайте экземпляр Retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("https://getpost-ilya1.up.railway.app")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Создайте экземпляр службы API
    val apiService = retrofit.create(ChangeUserURLService::class.java)

    // Создайте объект с новым именем пользователя
    val changeDCName = Change_DC(URL)

    // Отправьте POST-запрос
    val call = apiService.changeUserURL(uid, changeDCName)
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                // Запрос успешно отправлен
                Log.d("ChangeUserName", "ChangeUserName request successful")
                // Можете выполнить какие-либо дополнительные действия здесь
            } else {
                // Обработайте ошибку, если есть
                Log.e("ChangeUserName", "ChangeUserName request failed with code: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            // Обработайте ошибку при отправке запроса
            Log.e("ChangeUserName", "ChangeUserName request failed", t)
        }
    })
}
