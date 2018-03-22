package dev.olog.msc.presentation.dialog.explain.trial

import android.app.AlertDialog
import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.makeDialog

object ExplainTrialDialog {

    fun show(context: Context, positiveAction: () -> Unit){
        AlertDialog.Builder(context)
                .setTitle(R.string.trial_title)
                .setMessage(R.string.trial_message)
                .setPositiveButton(R.string.trial_positive_button, { _, _ -> positiveAction() })
                .makeDialog()
    }

}