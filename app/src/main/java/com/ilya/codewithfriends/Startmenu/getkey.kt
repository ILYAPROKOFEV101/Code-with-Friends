import android.content.Context
import com.ilya.reaction.logik.PreferenceHelper.saveDataToSharedPreferences
import okhttp3.*
import java.io.IOException

fun getKeyFromServer(uid: String, context: Context, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit) {
    val client = OkHttpClient()
    val url = "https://getpost-ilya1.up.railway.app/getkey/$uid"

    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            if (response.isSuccessful && body != null) {
                // Сохраняем ключ в SharedPreferences
                saveDataToSharedPreferences(context, "my_key", body)

                onSuccess(body)
                println("Received key: $body")
            } else {
                val error = IOException("Failed to get key. Error code: ${response.code}")
                onError(error)
                println("Failed to get key: ${error.message}")
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            onError(e)
            println("Request failed: ${e.message}")
        }
    })
}
