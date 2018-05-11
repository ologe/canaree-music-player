package dev.olog.msc.presentation.dialog.explain.trial

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.ThemedDialog
import dev.olog.msc.utils.k.extension.makeDialog

object ExplainTrialDialog {

    fun show(context: Context, positiveAction: () -> Unit){
        ThemedDialog.builder(context)
                .setTitle(R.string.trial_title)
                .setMessage(R.string.trial_message)
                .setPositiveButton(R.string.trial_positive_button, { _, _ -> positiveAction() })
                .makeDialog()
    }

}