package com.example.codewithfriends.Complaint

import com.example.codewithfriends.Startmenu.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface Complainttouser {
    @POST("Complaint")
    fun Sanduser(@Body request: Complaint): Call<Void>
}
