import android.util.Log
import com.ilya.codewithfriends.chattest.fragments.newUserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
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
