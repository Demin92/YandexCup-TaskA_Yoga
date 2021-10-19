package ru.demin.taska_yoga

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.Uri

fun sendEmail(
    context: Context,
    titleForChooser: String,
    email: String,
    subject: String,
    body: String? = null,
    cc: String? = null,
    onError: () -> Unit = {}
) {
    val sendIntent = Intent(ACTION_SEND).apply {
        data = Uri.parse("mailto:")
        type = "text/plain"
        putExtra(EXTRA_EMAIL, arrayOf(email))
        putExtra(EXTRA_SUBJECT, subject)
        body?.let { putExtra(EXTRA_TEXT, it) }
        cc?.let { putExtra(EXTRA_CC, it) }
    }

    try {
        context.startActivity(createChooser(sendIntent, titleForChooser))
    } catch (e: ActivityNotFoundException) {
        onError.invoke()
    }
}