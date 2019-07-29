package dev.olog.presentation.splash

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.presentation.R

object ExplainTrialDialog {

    fun show(context: Context, positiveAction: () -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.trial_title)
            .setMessage(R.string.trial_message)
            .setPositiveButton(R.string.trial_positive_button) { _, _ -> positiveAction() }
            .show()
    }

}