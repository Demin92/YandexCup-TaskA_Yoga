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
    private var averageNoise = 100

    private val breatheTickList = mutableListOf<BreatheTick>()
    private val report = mutableListOf<BreatheItem>()
    private val handler = Handler(Looper.getMainLooper())
    private val tickRunnable = object : Runnable {
        override fun run() {
            audioRecorder?.let { mediaRecorder ->
                breatheTickList.add(BreatheTick(System.currentTimeMillis(), mediaRecorder.maxAmplitude.also { volume ->
                    viewState.value = viewState.value!!.copy(volume = volume)
                }))
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

        report.forEach {
            Log.d("Povarity", "$it")
        }

        breatheTickList.clear()
        report.clear()
    }

    private fun measureAverageNoise() {

    }

    private fun createBreatheReport() {
        val inhaleThreshold = averageNoise * INHALE_KOEF
        val exhalationThreshold = averageNoise * EXHALATION_KOEF

        var lastMark: BreatheMark? = null
        var lastTime = 0L

        breatheTickList.forEachIndexed { index, tick ->
            when {
                index == 0 -> Unit
                index == breatheTickList.size - 1 -> Unit
                breatheTickList[index - 1].volume < exhalationThreshold && tick.volume >= exhalationThreshold && lastMark == BreatheMark.INHALE_END -> {
                    lastMark = BreatheMark.EXHALATION_START
                    report.add(BreatheItem(BreatheState.PAUSE, (tick.time - lastTime).toInt()))
                    lastTime = tick.time
                }
                tick.volume >= exhalationThreshold && breatheTickList[index + 1].volume < exhalationThreshold && lastMark == BreatheMark.EXHALATION_START -> {
                    lastMark = BreatheMark.EXHALATION_END
                    report.add(BreatheItem(BreatheState.EXHALATION, (tick.time - lastTime).toInt()))
                    lastTime = tick.time
                }
                breatheTickList[index - 1].volume < inhaleThreshold && tick.volume >= inhaleThreshold && (lastMark == null || lastMark == BreatheMark.EXHALATION_END) -> {
                    lastMark = BreatheMark.INHALE_START
                    if (lastTime == 0L) {
                        lastTime = tick.time
                    } else {
                        report.add(BreatheItem(BreatheState.PAUSE, (tick.time - lastTime).toInt()))
                        lastTime = tick.time
                    }
                }
                tick.volume >= inhaleThreshold && breatheTickList[index + 1].volume < inhaleThreshold && lastMark == BreatheMark.INHALE_START -> {
                    lastMark = BreatheMark.INHALE_END
                    report.add(BreatheItem(BreatheState.INHALE, (tick.time - lastTime).toInt()))
                    lastTime = tick.time
                }
            }
        }
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

    private data class BreatheTick(val time: Long, val volume: Int)

    private enum class BreatheMark {
        INHALE_START, INHALE_END, EXHALATION_START, EXHALATION_END
    }

    private enum class BreatheState {
        INHALE, PAUSE, EXHALATION
    }

    private data class BreatheItem(val state: BreatheState, val time: Int)


    companion object {
        private const val VOLUME_MEASURE_INTERVAL_IN_MILLIS = 100L
        private const val AUDIO_FILE_EXT = ".wav"
        private const val INHALE_KOEF = 1.4f
        private const val EXHALATION_KOEF = 4f
    }
}