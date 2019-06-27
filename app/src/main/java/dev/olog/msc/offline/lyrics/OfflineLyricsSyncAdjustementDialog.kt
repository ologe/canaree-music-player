package dev.olog.msc.offline.lyrics

import android.content.Context
import android.content.DialogInterface
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.enableForService
import dev.olog.shared.extensions.toast

object OfflineLyricsSyncAdjustementDialog {

    @JvmStatic
    fun show(ctx: Context, currentValue: String, positiveAction: (Long) -> Unit){
        val builder = AlertDialog.Builder(ctx)
                .setTitle(R.string.offline_lyrics_adjust_sync)
                .setView(R.layout.layout_edit_text_simple)
                .setPositiveButton(R.string.popup_positive_ok, null)
                .setNegativeButton(R.string.popup_negative_cancel, null)

        val dialog = builder.show()

        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)!!
        editText.setText(currentValue)
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)!!
        editTextLayout.hint = ctx.getString(R.string.offline_lyrics_adjust_sync_hint)

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val text = editText.text.toString()
            if (text.isDigitsOnly() || (text.isNotBlank() && text[0] == '-' && text.substring(1).isDigitsOnly())){
                positiveAction(text.toLong())
                dialog.dismiss()
            } else {
                ctx.toast(ctx.getString(R.string.offline_lyrics_adjust_sync_error))
            }
        }
    }

    @JvmStatic
    fun showForService(ctx: Context, currentValue: String, positiveAction: (Long) -> Unit){
        val builder = android.app.AlertDialog.Builder(ctx) // TODO try with androidx dialog
                .setTitle(R.string.offline_lyrics_adjust_sync)
                .setView(R.layout.content_layout_edit_text)
                .setPositiveButton(R.string.popup_positive_ok, null)
                .setNegativeButton(R.string.popup_negative_cancel, null)

        val dialog = builder.create()
        dialog.enableForService()
        dialog.show()

        val editText = dialog.findViewById<EditText>(R.id.editText)!!
        editText.setText(currentValue)
        editText.hint = ctx.getString(R.string.offline_lyrics_adjust_sync_hint)

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val text = editText.text.toString()
            if (text.isDigitsOnly() || (text.isNotBlank() && text[0] == '-' && text.substring(1).isDigitsOnly())){
                positiveAction(text.toLong())
                dialog.dismiss()
            } else {
                ctx.toast(ctx.getString(R.string.offline_lyrics_adjust_sync_error))
            }
        }
    }

}