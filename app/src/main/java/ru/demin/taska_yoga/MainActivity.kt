package ru.demin.taska_yoga

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(application) }
    private val minVolumeSize by lazy { resources.getDimension(R.dimen.min_volume_size) }
    private val maxVolumeSize by lazy { resources.getDimension(R.dimen.max_volume_size) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!RecordAudioPermissionHelper.hasRecordAudioPermission(this)) {
            RecordAudioPermissionHelper.requestRecordAudioPermission(this)
        } else {
            viewModel.onPermissionGranted()
        }

        btn.setOnClickListener { viewModel.onButtonClick() }
        viewModel.viewState.observe(this, ::render)
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
            ).show()

            if (!RecordAudioPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                RecordAudioPermissionHelper.launchPermissionSettings(this)
            }
        } else {
            viewModel.onPermissionGranted()
        }
    }

    private fun render(state: ViewState) {
        btn.isEnabled = !state.isAverageNoiseMeasuring
        if (state.isAverageNoiseMeasuring) {
            average_noise_text.isVisible = true
            return
        }
        average_noise_text.isVisible = false
        btn.text = resources.getString(if (state.isTrainingStarted) R.string.stop else R.string.start)
        volume_view.isVisible = state.isTrainingStarted && state.volume > 0
        if (state.isTrainingStarted) {
            volume_view.updateLayoutParams<ViewGroup.LayoutParams> {
                val size =  (state.volume * VOLUME_VISUAL_KOEF).coerceIn(minVolumeSize, maxVolumeSize).toInt()
                width = size
                height = size
            }
        }
    }

    companion object {
        private const val VOLUME_VISUAL_KOEF = 0.33f
    }
}