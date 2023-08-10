package com.example.codewithfriends.findroom.chats

interface MassageServis {

    suspend fun getAllMassages(){

    }

    companion object{
        val BASE_URL = "getpost-ilya1.up.railway.app/chat/"
    }
}