package ru.demin.taska_yoga

import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private var audioRecorder: MediaRecorder? = null
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start.setOnClickListener {
            if (!RecordAudioPermissionHelper.hasRecordAudioPermission(this)) {
                RecordAudioPermissionHelper.requestRecordAudioPermission(this)
            } else {
                start()

                countDownTimer = object : CountDownTimer(60_000, 100) {
                    override fun onTick(tickTime: Long) {
                        val volume = getVolume()
                        Log.d(TAG, "tickTime = $tickTime volume = $volume")
                    }
                    override fun onFinish() = Unit
                }.apply { start() }
            }
        }

        stop.setOnClickListener {
            stop()
            countDownTimer?.cancel()
            countDownTimer = null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!RecordAudioPermissionHelper.hasRecordAudioPermission(this)) {
            Toast.makeText(
                this,
                "Record Audio permission is needed to run this application",
                Toast.LENGTH_LONG
            )
                .show()
            if (!RecordAudioPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                RecordAudioPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }

        recreate()
    }


    private fun start() {
        audioRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(getAudioPath())
            prepare()
            start()
        }
    }

    private fun getAudioPath(): String {
        return "${cacheDir.absolutePath}${File.pathSeparator}${System.currentTimeMillis()}.wav"
    }

    private fun getVolume() = audioRecorder?.maxAmplitude ?: 0

    private fun stop() {
        audioRecorder?.let {
            it.stop()
            it.release()
        }
        audioRecorder = null
    }

    companion object {
        private const val TAG = "Povarity"
    }
}