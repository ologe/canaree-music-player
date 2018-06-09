package dev.olog.msc.presentation.theme

import android.app.AlertDialog
import android.content.Context

object ThemedDialog {

    @JvmStatic
    fun builder(context: Context): AlertDialog.Builder {
        return AlertDialog.Builder(context)
//        return when {
//            AppTheme.isDarkMode() -> AlertDialog.Builder(context, R.style.AppThemeDark)
//            AppTheme.isBlackMode() -> AlertDialog.Builder(context, R.style.AppThemeBlack)
//            else -> AlertDialog.Builder(context)
//        }
    }


}