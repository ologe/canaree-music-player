package dev.olog.presentation.dialogs.playlist.create

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseEditTextDialog
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments
import timber.log.Timber
import javax.inject.Inject

class PlayingQueueNewPlaylistDialog : BaseEditTextDialog() {

    companion object {
        const val TAG = "NewPlaylistDialog"

        @JvmStatic
        fun newInstance(): PlayingQueueNewPlaylistDialog {
            return PlayingQueueNewPlaylistDialog().withArguments()
        }
    }

    @Inject lateinit var presenter: NewPlaylistDialogPresenter

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return super.extendBuilder(builder)
            .setTitle(R.string.popup_new_playlist)
            .setPositiveButton(R.string.popup_positive_create, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    override fun setupEditText(layout: TextInputLayout, editText: TextInputEditText) {
        editText.hint = getString(R.string.popup_new_playlist)
    }

    override fun provideMessageForBlank(): String {
        return getString(R.string.popup_playlist_name_not_valid)
    }

    override suspend fun onItemValid(string: String) {
        var message: String
        try {
            presenter.savePlayingQueue(string)
            message = successMessage(act, string).toString()
        } catch (ex: Exception) {
            Timber.e(ex)
            message = getString(R.string.popup_error_message)
        }
        act.toast(message)
    }


    private fun successMessage(context: Context, currentValue: String): CharSequence {
        return context.getString(R.string.queue_saved_as_playlist, currentValue)
    }
}