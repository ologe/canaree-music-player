package dev.olog.msc.utils.k.extension

import android.app.AlertDialog
import android.content.DialogInterface
import android.support.v4.content.ContextCompat
import dev.olog.msc.R

private fun AlertDialog.tintPositiveButton(){
    getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(context!!, R.color.item_selected))
}

private fun AlertDialog.tintNegativeButton(){
    getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(context!!, R.color.dark_grey))
}

private fun AlertDialog.tintNeutralButton(){
    getButton(DialogInterface.BUTTON_NEUTRAL)
            .setTextColor(ContextCompat.getColor(context!!, R.color.dark_grey))
}

fun AlertDialog.Builder.makeDialog(): AlertDialog{
    val dialog = this.show()
    dialog.tintPositiveButton()
    dialog.tintNegativeButton()
    dialog.tintNeutralButton()
    return dialog
}