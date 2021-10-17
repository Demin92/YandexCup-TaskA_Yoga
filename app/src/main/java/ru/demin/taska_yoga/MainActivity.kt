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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            if (!RecordAudioPermissionHelper.hasRecordAudioPermission(this)) {
                RecordAudioPermissionHelper.requestRecordAudioPermission(this)
            } else {
                viewModel.onButtonClick()
            }
        }
        viewModel.viewState.observe(this) { state ->
            btn.text = resources.getString(if (state.isTrainingStarted) R.string.stop else R.string.start)
            volume_view.isVisible = state.isTrainingStarted && state.volume > 0
            if (state.isTrainingStarted) {
                volume_view.updateLayoutParams<ViewGroup.LayoutParams> {
                    width = (state.volume * VOLUME_VISUAL_KOEF).toInt()
                    height = (state.volume * VOLUME_VISUAL_KOEF).toInt()
                }
            }
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
            ).show()

            if (!RecordAudioPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                RecordAudioPermissionHelper.launchPermissionSettings(this)
            }
        } else {
            viewModel.onButtonClick()
        }
    }

    companion object {
        private const val VOLUME_VISUAL_KOEF = 0.05f
    }
}