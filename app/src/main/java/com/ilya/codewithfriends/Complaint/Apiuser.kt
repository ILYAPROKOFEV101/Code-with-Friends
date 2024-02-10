package com.ilya.codewithfriends.Complaint

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Complainttouser {
    @POST("Complaint")
    fun Sanduser(@Body request: Complaint): Call<Void>
}
