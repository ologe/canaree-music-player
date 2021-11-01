package dev.olog.offlinelyrics

import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object EditLyricsDialog {

    fun show(context: Context, currentText: String, updateFunc: (String) -> Unit) {
        val themeWrapper = ContextThemeWrapper(context, dev.olog.shared.android.R.style.AppTheme)
        val builder = MaterialAlertDialogBuilder(themeWrapper)
            .setTitle(R.string.offline_lyrics_edit_title)
            .setView(R.layout.layout_edit_text)
            .setPositiveButton("OK", null)
            .setNegativeButton("Back", null)

        val dialog = builder.create()
        if (context is Service){
            dialog.enableForService()
        }
        dialog.show()

        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)!!
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.wrapper)!!
        editTextLayout.hint = context.getString(R.string.offline_lyrics_edit_hint)
        if (currentText != context.getString(R.string.offline_lyrics_empty)) {
            editText.setText(currentText)
        }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            updateFunc(editText.text.toString())
            dialog.dismiss()
        }

        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
            editText.setText("")
            dialog.dismiss()
        }
    }

}