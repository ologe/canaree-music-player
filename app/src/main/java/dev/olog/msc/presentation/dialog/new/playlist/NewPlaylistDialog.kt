package dev.olog.msc.presentation.dialog.new.playlist

import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseEditTextDialog
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.withArguments
import javax.inject.Inject

class NewPlaylistDialog : BaseEditTextDialog() {

    companion object {
        const val TAG = "NewPlaylistDialog"
        const val ARGUMENTS_MEDIA_ID = "${TAG}.arguments.media_id"

        fun newInstance(mediaId: MediaId): NewPlaylistDialog {
            return NewPlaylistDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString())
        }
    }

    @Inject lateinit var presenter: NewPlaylistDialogPresenter

    override fun provideDialogTitle(): Int = R.string.popup_new_playlist

    override fun providePositiveMessage(): Int = R.string.popup_positive_create

    override fun provideErrorMessageForBlankForm(): Int = R.string.popup_playlist_name_not_valid

    override fun provideErrorMessageForInvalidForm(string: String): Int = R.string.popup_playlist_name_already_exist

    override fun onValidData(string: String) {
        presenter.execute(string)
                .subscribe({}, Throwable::printStackTrace)
    }

    override fun isStringValid(string: String): Boolean = presenter.isStringValid(string)

}