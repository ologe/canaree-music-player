package dev.olog.msc.offline.lyrics

import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.widget.EditText
import androidx.core.text.isDigitsOnly
import androidx.core.widget.toast
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.ThemedDialog
import dev.olog.msc.utils.k.extension.enableForService
import dev.olog.msc.utils.k.extension.makeDialog

object OfflineLyricsSyncAdjustementDialog {

    fun show(ctx: Context, currentValue: String, forService: Boolean, positiveAction: (Long) -> Unit){
        val builder = ThemedDialog.builder(ctx)
                .setTitle(R.string.offline_lyrics_adjust_sync)
                .setView(if (forService) R.layout.content_layout_edit_text else R.layout.layout_edit_text_simple)
                .setPositiveButton(R.string.popup_positive_ok, null)
                .setNegativeButton(R.string.popup_negative_cancel, null)

        var dialog = builder.create()
        if (forService) {
            dialog.enableForService()
            dialog.show()
        } else {
            dialog = builder.makeDialog()
        }

        val editText : EditText = dialog.findViewById<TextInputEditText>(R.id.editText)
        editText.setText(currentValue)
        if (!forService){
            val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)
            editTextLayout.hint = ctx.getString(R.string.offline_lyrics_adjust_sync_hint)
        } else {
            editText.hint = ctx.getString(R.string.offline_lyrics_adjust_sync_hint)
        }

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