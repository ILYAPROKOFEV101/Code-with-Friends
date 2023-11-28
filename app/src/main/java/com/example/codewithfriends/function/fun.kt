

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codewithfriends.findroom.FindRoom


import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class LoadingComponent {




    fun userexsist(uid: String,  context: Context) {
        // Создаем клиент OkHttp
        val client = OkHttpClient()


        // Создаем запрос
        val request = Request.Builder()
            .url("https://getpost-ilya1.up.railway.app/examination/$uid")
            .build()

        // Выполняем запрос
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                // Ошибка
                Log.e("getData", e.message ?: "Неизвестная ошибка")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    // Получаем данные
                    val data = response.body!!.string()
                    val trueOrFalse = data.toBoolean()


                    if(trueOrFalse){
                        (context as? Activity)?.finishAffinity()
                        // Показать Toast уведомление
                        Toast.makeText(context, "This is a ban", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    // Ошибка
                    Log.e("getData", "Ошибка получения данных: ${response.code}")
                }
            }
        })
    }

}

class MyApp : Application() {
    var isAppInForeground = false

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(AppLifecycleCallbacks())
    }

    private inner class AppLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityStarted(activity: Activity) {
            isAppInForeground = true
        }

        override fun onActivityResumed(activity: Activity) {
            isAppInForeground = true
        }

        override fun onActivityPaused(activity: Activity) {
            isAppInForeground = false
        }

        override fun onActivityStopped(activity: Activity) {
            isAppInForeground = false
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }
    }
}
