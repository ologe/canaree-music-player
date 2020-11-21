package dev.olog.lib.offline.lyrics

import android.app.Service
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.text.isDigitsOnly
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.olog.shared.android.extensions.toast

object OfflineLyricsSyncAdjustementDialog {

    fun show(ctx: Context, currentValue: String, positiveAction: (Long) -> Unit) {
        val themeWrapper = ContextThemeWrapper(ctx, R.style.AppTheme)
        val builder = MaterialAlertDialogBuilder(themeWrapper)
            .setTitle(R.string.offline_lyrics_adjust_sync)
            .setView(R.layout.layout_edit_text_simple)
            .setPositiveButton("OK", null)
            .setNegativeButton("Back", null)



        val dialog = builder.create()
        if (ctx is Service){
            dialog.enableForService()
        }
        dialog.show()

        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)!!
        editText.setText(currentValue)
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)!!
        editTextLayout.hint = ctx.getString(R.string.offline_lyrics_adjust_sync_hint)

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val text = editText.text.toString()
            if (text.isDigitsOnly() || (text.isNotBlank() && text[0] == '-' && text.substring(1).isDigitsOnly())) {
                positiveAction(text.toLong())
                dialog.dismiss()
            } else {
                ctx.toast(ctx.getString(R.string.offline_lyrics_adjust_sync_error))
            }
        }
    }

}