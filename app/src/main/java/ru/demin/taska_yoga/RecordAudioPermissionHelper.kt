package ru.demin.taska_yoga

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object RecordAudioPermissionHelper {
    private const val PERMISSION_CODE = 0
    private const val RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO

    /** Check to see we have the necessary permissions for this app.  */
    fun hasRecordAudioPermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(activity, RECORD_AUDIO_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

    /** Check to see we have the necessary permissions for this app, and ask for them if we don't.  */
    fun requestRecordAudioPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(RECORD_AUDIO_PERMISSION), PERMISSION_CODE
        )
    }

    /** Check to see if we need to show the rationale for this permission.  */
    fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, RECORD_AUDIO_PERMISSION)
    }

    /** Launch Application Setting to grant permission.  */
    fun launchPermissionSettings(activity: Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }
}
