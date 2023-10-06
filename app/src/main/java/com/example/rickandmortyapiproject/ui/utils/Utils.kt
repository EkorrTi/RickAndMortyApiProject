package com.example.rickandmortyapiproject.ui.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.example.rickandmortyapiproject.R

object Utils {
    const val ID_START_INDEX_CHARACTER = 42
    const val ID_START_INDEX_LOCATION = 41
    const val ID_START_INDEX_EPISODE = 40

    fun onErrorResponse(context: Context, e: Throwable) {
        if (e.message == "timeout")
            showAlertDialog(context, R.string.timeout_exception)
        else
            showAlertDialog(context)
    }

    /**
     * Generic error dialog
     */
    fun showAlertDialog(context: Context) = showAlertDialog(context, R.string.error_text)

    /**
     * Generic error dialog with customizable message
     */
    fun showAlertDialog(
        context: Context,
        @StringRes message: Int
    ) = showAlertDialog(context, R.string.error, message)

    fun showAlertDialog(
        context: Context,
        @StringRes title: Int,
        @StringRes message: Int
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}