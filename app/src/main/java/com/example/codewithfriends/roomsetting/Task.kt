package com.example.codewithfriends.roomsetting


data class TaskData(
    val gitbranch: String,
    val filename: String,
    val photo: String,
    val mession: String,
    val id: String
)
// Если у вас есть какие-то данные, которые вы отправляете на сервер, создайте класс для этих данных тоже
data class DeleteRequest(
    val roomId: String,
    val id: String
)
