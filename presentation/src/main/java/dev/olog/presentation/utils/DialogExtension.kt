package dev.olog.presentation.utils

import android.app.AlertDialog
import android.content.DialogInterface
import android.support.v4.content.ContextCompat
import dev.olog.presentation.R

private fun AlertDialog.tintPositiveButton(){
    getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(context!!, R.color.item_selected))
}

fun AlertDialog.Builder.makeDialog(): AlertDialog{
    val dialog = this.show()
    dialog.tintPositiveButton()
    return dialog
}