package com.example.rickandmortyapiproject.util

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.example.rickandmortyapiproject.R

object Utils {
    private const val ID_START_INDEX_CHARACTER = 42
    private const val ID_START_INDEX_LOCATION = 41
    private const val ID_START_INDEX_EPISODE = 40

    fun extractEpisodeIds(episodes: List<String>): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (e in episodes)
            list.add(e.substring(ID_START_INDEX_EPISODE).toInt())
        return list
    }

    fun extractCharacterIds(characters: List<String>): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (c in characters)
            list.add(c.substring(ID_START_INDEX_CHARACTER).toInt())
        return list
    }

    fun extractCharacterLocationId(url: String): Int = url.substring(ID_START_INDEX_LOCATION).toInt()

    fun isConnectedToNetwork(context: Context) : Boolean {
        val conMan = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = conMan.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun onErrorResponse(context: Context, e: Throwable) {
        if (e.message == "timeout")
            showAlertDialog(context, R.string.timeout_exception)
        else
            showAlertDialog(context)

        Log.w("Response error", e.toString())
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