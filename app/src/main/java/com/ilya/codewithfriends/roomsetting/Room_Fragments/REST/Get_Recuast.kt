package com.ilya.codewithfriends.roomsetting.Room_Fragments.REST

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.ilya.codewithfriends.findroom.Room
import com.ilya.codewithfriends.roomsetting.Add_user_Invite
import com.ilya.codewithfriends.roomsetting.ui.theme.Participant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Get_Recuast {
    fun whoinroom(roomId: String, participantsState: MutableState<List<Participant>>, context: Context) {
        val url = "https://getpost-ilya1.up.railway.app/participants/$roomId"

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                Log.d("Mylog", "Result: $response")
                val gson = Gson()
                val participantListType = object : TypeToken<List<Participant>>() {}.type

                try {
                    val newParticipants: List<Participant> = gson.fromJson(response.toString(), participantListType)
                    participantsState.value = newParticipants
                } catch (e: JsonSyntaxException) {
                    Log.e("Mylog", "Error parsing JSON: $e")
                }
            },
            { error ->
                Log.d("Mylog", "Error: $error")
            }
        )

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(request)
    }


    fun getData(roomId: String, rooms: MutableState<List<Room>>, context: Context) {
        val url = "https://getpost-ilya1.up.railway.app/aboutroom/$roomId"

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                Log.d("Mylog", "Result: $response")
                val gson = Gson()
                val roomListType = object : TypeToken<List<Room>>() {}.type
                val utf8Response = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                val newRooms: List<Room> = gson.fromJson(utf8Response, roomListType)

                rooms.value = newRooms

                // Получение первой комнаты из списка (если она есть)
                val firstRoom = newRooms.firstOrNull()
                if (firstRoom != null) {
                    // Вызов Composable roomname() и передача имени комнаты

                }
            },
            { error ->
                Log.d("Mylog", "Error: $error")
            }
        )

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(request)
    }




}

fun addUser(uidAdmin: String, idRequest: String, uidUser: String, roomId: String) {
    val url = "https://getpost-ilya1.up.railway.app/" // Replace with your actual base URL

    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()) // Assuming JSON response format
        .build()

    val apiService = retrofit.create(Add_user_Invite::class.java)

    // Call the API method
    val call = apiService.putUSER(uidAdmin, idRequest, uidUser, roomId)

    // Execute the call asynchronously
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Log.d("ADDUSER" , "User added successfully $response")
            } else {
                Log.d("ADDUSER" , "User not added successfully $response")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            // Handle failure
            // Maybe show an error message or perform some other action
        }
    })
}

