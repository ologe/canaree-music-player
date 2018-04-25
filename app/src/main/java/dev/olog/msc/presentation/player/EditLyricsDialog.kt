package dev.olog.msc.presentation.player

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.makeDialog

object EditLyricsDialog {

    fun show(activity: Activity, currentText: String, updateFunc: (String) -> Unit){
        val builder = AlertDialog.Builder(activity)
                .setTitle(R.string.offline_lyrics_edit_title)
                .setView(R.layout.layout_edit_text)
                .setPositiveButton(R.string.popup_positive_ok, null)
                .setNegativeButton(R.string.popup_negative_back, null)
                .setCancelable(false)

        val dialog = builder.makeDialog()

        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)
        editTextLayout.hint = activity.getString(R.string.offline_lyrics_edit_hint)
        if (currentText != activity.getString(R.string.offline_lyrics_empty)){
            editText.setText(currentText)
        }
        dialog.findViewById<View>(R.id.clear).setOnClickListener { editText.setText("") }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            updateFunc(editText.text.toString())
            dialog.dismiss()
        }

        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
            editText.setText("")
            dialog.dismiss()
        }

        dialog.show()
    }

}