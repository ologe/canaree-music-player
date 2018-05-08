package dev.olog.msc.offline.lyrics

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import dev.olog.msc.R
import dev.olog.msc.theme.ThemedDialog
import dev.olog.msc.utils.isOreo
import dev.olog.msc.utils.k.extension.enableForService
import dev.olog.msc.utils.k.extension.makeDialog

object EditLyricsDialog {

    fun show(context: Context, currentText: String, updateFunc: (String) -> Unit){
        val builder = ThemedDialog.builder(context)
                .setTitle(R.string.offline_lyrics_edit_title)
                .setView(R.layout.layout_edit_text)
                .setPositiveButton(R.string.popup_positive_ok, null)
                .setNegativeButton(R.string.popup_negative_back, null)

        val dialog = builder.makeDialog()

        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)
        editTextLayout.hint = context.getString(R.string.offline_lyrics_edit_hint)
        if (currentText != context.getString(R.string.offline_lyrics_empty)){
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
    }

    fun showForService(context: Context, currentText: String, updateFunc: (String) -> Unit){
        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.offline_lyrics_edit_title)
                .setView(R.layout.content_layout_edit_text)
                .setPositiveButton(R.string.popup_positive_ok, null)
                .setNegativeButton(R.string.popup_negative_back, null)
                .setCancelable(false)


        val dialog = builder.create()
        dialog.enableForService()
        dialog.show()

        val editText = dialog.findViewById<EditText>(R.id.editText)
        if (currentText != context.getString(R.string.offline_lyrics_empty)){
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