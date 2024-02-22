package com.ilya.codewithfriends.Vois

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.FileOutputStream

class AndroidAudioRecoder(
    private val context: Context
): AudioRecoder {

    private var recoder: MediaRecorder? = null

    private fun createRecoder() : MediaRecorder{
        return  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun star(outputFile: File) {
            createRecoder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(FileOutputStream(outputFile).fd)

                prepare()
                start()

                recoder = this
            }
    }

    override fun stop() {
        recoder?.stop()
        recoder?.reset()
        recoder = null

    }
}