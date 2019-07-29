package dev.olog.offlinelyrics

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.EditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object EditLyricsDialog {

    fun show(context: Context, currentText: String, updateFunc: (String) -> Unit) {
        val builder = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.offline_lyrics_edit_title)
            .setView(R.layout.layout_edit_text)
            .setPositiveButton("OK", null) // TODO
            .setNegativeButton("Back", null)

        val dialog = builder.show()

        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)!!
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)!!
        editTextLayout.hint = context.getString(R.string.offline_lyrics_edit_hint)
        if (currentText != context.getString(R.string.offline_lyrics_empty)) {
            editText.setText(currentText)
        }
        dialog.findViewById<View>(R.id.clear)!!.setOnClickListener { editText.setText("") }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            updateFunc(editText.text.toString())
            dialog.dismiss()
        }

        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
            editText.setText("")
            dialog.dismiss()
        }
    }

    fun showForService(context: Context, currentText: String, updateFunc: (String) -> Unit) {
        val builder = MaterialAlertDialogBuilder(context, R.style.AppTheme)
            .setTitle(R.string.offline_lyrics_edit_title)
            .setView(R.layout.content_layout_edit_text)
            .setPositiveButton("OK", null) // TODO
            .setNegativeButton("Back", null)
            .setCancelable(false)


        val dialog = builder.create()
        dialog.enableForService()
        dialog.show()

        val editText = dialog.findViewById<EditText>(R.id.editText)!!
        if (currentText != context.getString(R.string.offline_lyrics_empty)) {
            editText.setText(currentText)
        }
        dialog.findViewById<View>(R.id.clear)!!.setOnClickListener { editText.setText("") }

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