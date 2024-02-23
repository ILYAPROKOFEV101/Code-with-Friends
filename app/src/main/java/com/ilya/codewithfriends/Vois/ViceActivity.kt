package com.ilya.codewithfriends.Vois

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ilya.codewithfriends.Vois.ui.theme.CodeWithFriendsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.Socket

class ViceActivity : ComponentActivity() {

    private var recorder: AudioRecord? = null
    private var bufferSize: Int = 0
    private var isRecording by mutableStateOf(false)
    private lateinit var audioFile: File
    private lateinit var audioSender: AudioSender
    private val webSocketUrl = "wss://getpost-ilya1.up.railway.app/file" // Замените на ваш URL WebSocket

    private var audioSendJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)

        setContent {
            MainContent()
        }
    }

    @Composable
    private fun MainContent() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RecordButton()
            }
        }
    }

    @Composable
    private fun RecordButton() {
        Button(
            onClick = {
                // Toggle recording state
                isRecording = !isRecording
                if (isRecording) {
                    startRecording()
                } else {
                    stopRecording()
                }
            }
        ) {
            Text(text = if (isRecording) "Stop Recording" else "Start Recording")
        }
    }

    private fun startRecording() {
        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        bufferSize = if (minBufferSize > 0) minBufferSize else SAMPLE_RATE * 2

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Handle permissions
            return
        }
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        recorder?.startRecording()

        isRecording = true

        // Create a file for audio recording
        audioFile = File(
            externalCacheDir?.absolutePath ?: cacheDir.absolutePath,
            "audio.wav"
        )

        // Start writing the audio data to the file
        GlobalScope.launch(Dispatchers.IO) {
            val outputStream = FileOutputStream(audioFile)
            val buffer = ByteArray(bufferSize)

            while (isRecording) {
                val bytesRead = recorder?.read(buffer, 0, bufferSize) ?: 0
                if (bytesRead > 0) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }

            // Stop recording and close the file output stream
            recorder?.stop()
            recorder?.release()
            outputStream.close()
        }

        // Start sending audio periodically
        startSendingAudioPeriodically()
    }

    private fun stopRecording() {
        isRecording = false
        // Stop sending audio periodically
        stopSendingAudioPeriodically()
    }

    private fun startSendingAudioPeriodically() {
        audioSendJob?.cancel() // Cancel the previous job if exists

        audioSendJob = GlobalScope.launch(Dispatchers.IO) {
            while (isRecording) {
                if (audioFile.exists()) {
                    sendAudio(audioFile) // Send audio if the file exists
                }
                delay(1000) // Delay for 1 second
            }
        }
    }

    private fun stopSendingAudioPeriodically() {
        audioSendJob?.cancel() // Cancel sending audio
    }

    private fun sendAudio(audioFile: File) {
        // Send audio via WebSocket
        audioSender = AudioSender(webSocketUrl)
        audioSender.sendAudio(audioFile)
    }

    companion object {
        const val SAMPLE_RATE = 44100 // Change to your desired sample rate
    }
}