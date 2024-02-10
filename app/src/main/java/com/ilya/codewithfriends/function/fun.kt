

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast


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
