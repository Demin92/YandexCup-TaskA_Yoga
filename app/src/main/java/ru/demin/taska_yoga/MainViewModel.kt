package ru.demin.taska_yoga

import android.app.Application
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class MainViewModel(private val context: Application) : ViewModel() {

    private var audioRecorder: MediaRecorder? = null

    private val volumeMap = mutableMapOf<Long, Int>()
    private val handler = Handler(Looper.getMainLooper())
    private val tickRunnable = object : Runnable {
        override fun run() {
            audioRecorder?.let { mediaRecorder ->
                volumeMap[System.currentTimeMillis()] = mediaRecorder.maxAmplitude.also {
                    viewState.value = viewState.value!!.copy(volume = it)
                }
            }
            handler.postDelayed(this, VOLUME_MEASURE_INTERVAL_IN_MILLIS)
        }
    }

    val viewState = MutableLiveData(ViewState())

    fun onButtonClick() {
        if (viewState.value!!.isTrainingStarted) onStopClick() else onStartClick()
    }

    private fun onStartClick() {
        audioRecorder = createMediaRecorder().apply { start() }
        tickRunnable.run()
        viewState.value = ViewState(true, audioRecorder!!.maxAmplitude)
    }

    private fun onStopClick() {
        viewState.value = ViewState()
        stopRecorder()
        handler.removeCallbacks(tickRunnable)
        createBreatheReport()
        sendBreatheReport()

        volumeMap.onEach { Log.d("Povarity", "${it.key} ${it.value}") }

        volumeMap.clear()
    }

    private fun createBreatheReport() {

    }

    private fun sendBreatheReport() {

    }

    private fun createMediaRecorder() = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setOutputFile(getAudioPath())
        prepare()
    }

    private fun getAudioPath(): String {
        return "${context.cacheDir.absolutePath}${File.pathSeparator}${System.currentTimeMillis()}$AUDIO_FILE_EXT"
    }

    private fun stopRecorder() {
        audioRecorder?.run { stop();release() }
        audioRecorder = null
    }

    companion object {
        private const val VOLUME_MEASURE_INTERVAL_IN_MILLIS = 100L
        private const val AUDIO_FILE_EXT = ".wav"
    }


}