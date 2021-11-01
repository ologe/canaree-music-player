package dev.olog.presentation.dialogs

import android.app.Activity
import android.app.Dialog
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.utils.isQ
import kotlinx.coroutines.launch

abstract class BaseDialog : DialogFragment() {

    companion object {
        const val ACCESS_CODE = 101
    }

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

    protected suspend fun catchRecoverableSecurityException(
        fragment: Fragment,
        action: suspend () -> Unit
    ) {
        if (isQ()) {
            try {
                action()
            } catch (rse: RecoverableSecurityException) {
                val requestAccessIntentSender = rse.userAction.actionIntent.intentSender

                // In your code, handle IntentSender.SendIntentException.
                fragment.startIntentSenderForResult(
                    requestAccessIntentSender, ACCESS_CODE,
                    null, 0, 0, 0, null
                )
            }
        } else {
            action()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACCESS_CODE && resultCode == Activity.RESULT_OK){
            lifecycleScope.launch { onRecoverableSecurityExceptionRecovered() }
        }
    }

    protected open suspend fun onRecoverableSecurityExceptionRecovered() {

    }

}