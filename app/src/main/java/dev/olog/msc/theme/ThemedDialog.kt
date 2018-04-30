package dev.olog.msc.theme

import android.app.AlertDialog
import android.content.Context
import dev.olog.msc.R

object ThemedDialog {

    fun builder(context: Context): AlertDialog.Builder {
        return when {
            AppTheme.isDarkMode() -> AlertDialog.Builder(context, R.style.DarkAlert)
            AppTheme.isBlackMode() -> AlertDialog.Builder(context, R.style.BlackAlert)
            else -> AlertDialog.Builder(context)
        }
    }

}