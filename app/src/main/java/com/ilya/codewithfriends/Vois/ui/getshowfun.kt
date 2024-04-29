import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

suspend fun getSettingFromServer(): Boolean = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://getpost-ilya1.up.railway.app/getsetting")
        .build()

    try {
        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            return@withContext response.isSuccessful && responseBody == "true"
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return@withContext false
    }
}