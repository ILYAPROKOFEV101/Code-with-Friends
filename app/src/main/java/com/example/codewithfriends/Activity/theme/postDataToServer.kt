

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MyApiClient {

    fun postDataToServer(userData: Map<String, String>) {
        val url = "http://127.0.0.1:8073/customer" // Замените на свой URL сервера

        val json = JSONObject(userData)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                // Здесь можно обработать успешный ответ сервера, если требуется
            } else {
                // Здесь можно обработать неуспешный ответ сервера, если требуется
            }
        } catch (e: IOException) {
            // Обработка ошибки при выполнении запроса
            e.printStackTrace()
        }
    }
}

