package dev.olog.presentation.dialog_rename

import dev.olog.presentation.R
import dev.olog.presentation._base.BaseEditTextDialog
import dev.olog.presentation.utils.extension.withArguments
import dev.olog.shared.MediaId
import javax.inject.Inject

class RenameDialog : BaseEditTextDialog() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: MediaId, itemTitle: String): RenameDialog {
            return RenameDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject lateinit var presenter: RenameDialogPresenter

    override fun provideDialogTitle(): Int = R.string.popup_rename

    override fun providePositiveMessage(): Int = R.string.popup_positive_rename

    override fun provideErrorMessageForEmptyString(): Int = R.string.popup_playlist_name_not_valid

    override fun provideErrorMessageForInvalidString(string: String): Int = R.string.popup_playlist_name_already_exist

    override fun onValidData(string: String) {
        val oldTitle = arguments!!.getString(ARGUMENTS_ITEM_TITLE)
        presenter.execute(oldTitle, string)
                .subscribe({}, Throwable::printStackTrace)
    }

    override fun isStringValid(string: String): Boolean = presenter.checkData(string)

    override fun provideStartEditTextValue(): String {
        return arguments!!.getString(ARGUMENTS_ITEM_TITLE)
    }
}