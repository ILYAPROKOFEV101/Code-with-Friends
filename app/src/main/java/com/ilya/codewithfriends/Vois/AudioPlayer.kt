package com.ilya.codewithfriends.Vois

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}