import android.util.Log
import com.google.gson.Gson
import com.ilya.codewithfriends.Startmenu.Add_User
import com.ilya.codewithfriends.Startmenu.new_User
import com.ilya.codewithfriends.chattest.fragments.Friends
import com.ilya.codewithfriends.chattest.fragments.MyFrends
import com.ilya.codewithfriends.chattest.fragments.newUserData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path




interface GetUserByNameService {
    @GET("/getUserByName/{username}")
    fun getUserByName(@Path("username") username: String): Call<List<newUserData>>
}


fun getUserByName(username: String, onResponse: (List<newUserData>) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://getpost-ilya1.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(GetUserByNameService::class.java)

    val call = service.getUserByName(username)
    call.enqueue(object : Callback<List<newUserData>> {
        override fun onResponse(call: Call<List<newUserData>>, response: Response<List<newUserData>>) {
            if (response.isSuccessful) {
                val userDataList = response.body()
                if (userDataList != null) {
                    // Успешно получены данные
                    onResponse(userDataList)
                } else {
                    // Пустой ответ
                    Log.e("UserData", "Получен пустой ответ")
                    onResponse(emptyList())
                }
            } else {
                // Ошибка при получении данных
                Log.e("UserData", "Ошибка при получении данных: ${response.code()}")
                onResponse(emptyList())
            }
        }

        override fun onFailure(call: Call<List<newUserData>>, t: Throwable) {
            // Ошибка при выполнении запроса
            Log.e("UserData", "Ошибка при выполнении запроса: ${t.message}")
            onResponse(emptyList())
        }
    })
}


// Определение интерфейса для API
interface Add_Friends {
    @POST("/andsoket")
    fun Sanduser(@Body userData: RequestBody): Call<Void>
}

// Функция для отправки запроса
fun addsoket(uidone: String, soket: String, uidtwo: String) {

    // Создание экземпляра Retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("https://getpost-ilya1.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Создание экземпляра службы API
    val apiService = retrofit.create(Add_Friends::class.java)

    // Создание объекта Friends с вашими данными
    val userData = Friends(soket, uidone, uidtwo)

    // Преобразование объекта Friends в JSON-строку
    val requestBody = Gson().toJson(userData).toRequestBody("application/json".toMediaTypeOrNull())

    // Отправка POST-запроса
    val call = apiService.Sanduser(requestBody)
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                // Запрос успешно отправлен
                Log.d("Adduser", "Adduser request successful")
                // Можно выполнить какие-либо дополнительные действия здесь
            } else {
                // Обработка ошибки, если есть
                Log.e("Adduser", "Adduser request failed with code: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            // Обработка ошибки при отправке запроса
            Log.e("Adduser", "Adduser request failed", t)
        }
    })
}


interface Find_Frends {
    @GET("/getUserFriends/{uid}")
    fun getUserByName(@Path("uid") uid: String): Call<List<MyFrends>>
}


fun Find_frends(uid: String, onResponse: (List<MyFrends>) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://getpost-ilya1.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(Find_Frends::class.java)

    val call = service.getUserByName(uid)
    call.enqueue(object : Callback<List<MyFrends>> {
        override fun onResponse(call: Call<List<MyFrends>>, response: Response<List<MyFrends>>) {
            if (response.isSuccessful) {
                val userDataList = response.body()
                if (userDataList != null) {
                    // Успешно получены данные
                    onResponse(userDataList)
                } else {
                    // Пустой ответ
                    Log.e("UserData", "Получен пустой ответ")
                    onResponse(emptyList())
                }
            } else {
                // Ошибка при получении данных
                Log.e("UserData", "Ошибка при получении данных: ${response.code()}")
                onResponse(emptyList())
            }
        }

        override fun onFailure(call: Call<List<MyFrends>>, t: Throwable) {
            // Ошибка при выполнении запроса
            Log.e("UserData", "Ошибка при выполнении запроса: ${t.message}")
            onResponse(emptyList())
        }
    })
}


