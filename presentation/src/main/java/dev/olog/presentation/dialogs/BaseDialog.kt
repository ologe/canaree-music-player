package dev.olog.presentation.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.presentation.base.BaseDialogFragment
import dev.olog.shared.android.extensions.act
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

abstract class BaseDialog : BaseDialogFragment(), CoroutineScope by MainScope() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var builder = MaterialAlertDialogBuilder(act)
        builder = extendBuilder(builder)

        val dialog = builder.show()
        extendDialog(dialog)

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            positionButtonAction(act)
        }

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
            negativeButtonAction(act)
        }
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
            neutralButtonAction(act)
        }

        return dialog
    }

    protected abstract fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder
    protected open fun extendDialog(dialog: AlertDialog) {}

    protected open fun positionButtonAction(context: Context) {}
    protected open fun negativeButtonAction(context: Context) {
        dismiss()
    }
    protected open fun neutralButtonAction(context: Context) {}

}