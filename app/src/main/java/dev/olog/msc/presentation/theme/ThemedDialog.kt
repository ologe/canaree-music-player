package dev.olog.msc.presentation.theme

import android.content.Context
import android.support.v7.app.AlertDialog

object ThemedDialog {

    @JvmStatic
    fun builder(context: Context): AlertDialog.Builder {
        return AlertDialog.Builder(context)
    }

    @JvmStatic
    fun frameworkBuilder(context: Context): android.app.AlertDialog.Builder {
        return android.app.AlertDialog.Builder(context)
    }

}