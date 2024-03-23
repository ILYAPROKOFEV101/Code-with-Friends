package com.ilya.codewithfriends.roomsetting


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

data class ids(
    val socketid: String,
    val ids: String,
    val uids: String,
)

data class Room2(
    val id: String,
    val roomName: String,
    val language: String,
    val placeInRoom: Int,
    val aboutRoom: String,
    val Admin: String,
    val url: String
)

data class Over_DeletetItem(
    val deletet: String,
    val over: String
)

data class Usrs_ivite (
    val uid: String,
    val name:String,
    val url: String,
    val id:String
)



