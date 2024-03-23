package com.ilya.codewithfriends.Vois

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileInputStream


class AudioSender(private val webSocketUrl: String) {

    private var webSocket: okhttp3.WebSocket? = null

    fun sendAudio(audioFile: File) {
        val client = OkHttpClient.Builder().build()
        val request = okhttp3.Request.Builder().url(webSocketUrl).build()

        client.newWebSocket(request, object : okhttp3.WebSocketListener() {
            override fun onOpen(webSocket: okhttp3.WebSocket, response: okhttp3.Response) {
                super.onOpen(webSocket, response)
                this@AudioSender.webSocket = webSocket

                GlobalScope.launch(Dispatchers.IO) {
                    val inputStream = FileInputStream(audioFile)
                    val buffer = ByteArray(audioFile.length().toInt())
                    inputStream.read(buffer)
                    inputStream.close()

                    // Send the content of the file via WebSocket
                    webSocket.send(okio.ByteString.of(*buffer))
                    // Send a message indicating the end of file transmission
                    webSocket.send("EndOfFile")
                }
            }

            override fun onFailure(webSocket: okhttp3.WebSocket, t: Throwable, response: okhttp3.Response?) {
                super.onFailure(webSocket, t, response)
                // Handle errors
            }
        })
    }

    fun stopSending() {
        webSocket?.close(500, null)
    }
}


class AudioPlayer {

    private var audioTrack: AudioTrack? = null

    fun play(audioData: ByteArray) {
        // Stop any ongoing playback
        stop()

        // Create an AudioTrack
        val minBufferSize = AudioTrack.getMinBufferSize(
            44100,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        audioTrack = AudioTrack(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build(),
            AudioFormat.Builder()
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(44100)
                .build(),
            minBufferSize,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )

        // Start playback
        audioTrack?.play()

        // Write data to the AudioTrack
        audioTrack?.write(audioData, 0, audioData.size)

        // Wait for playback to finish
        while (audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) {
            // Do nothing, just wait
        }

        // Release resources
        stop()
    }

    private fun stop() {
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }
}


