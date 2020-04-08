package dev.olog.presentation.dialogs.playlist.rename

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseEditTextDialog
import dev.olog.shared.android.extensions.getArgument
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import timber.log.Timber
import javax.inject.Inject

class RenameDialog : BaseEditTextDialog() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "${TAG}_arguments_media_id"
        const val ARGUMENTS_ITEM_TITLE = "${TAG}_arguments_item_title"

        @JvmStatic
        fun newInstance(mediaId: PresentationId.Category, itemTitle: String): RenameDialog {
            return RenameDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject lateinit var presenter: RenameDialogPresenter

    private val mediaId by lazyFast {
        getArgument<PresentationId.Category>(ARGUMENTS_MEDIA_ID)
    }
    private val itemTitle by lazyFast { getArgument<String>(ARGUMENTS_ITEM_TITLE) }

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return super.extendBuilder(builder)
            .setTitle(R.string.popup_rename)
            .setPositiveButton(R.string.popup_positive_rename, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    override fun setupEditText(layout: TextInputLayout, editText: TextInputEditText) {
        editText.setText(getArgument<String>(ARGUMENTS_ITEM_TITLE))
    }

    override fun provideMessageForBlank(): String {
        return getString(R.string.popup_playlist_name_not_valid)
    }

    override suspend fun onItemValid(string: String) {
        var message: String
        try {
            presenter.execute(mediaId, string)
            message = successMessage(requireActivity(), string)
        } catch (ex: Exception) {
            Timber.e(ex)
            message = getString(R.string.popup_error_message)
        }
        requireActivity().toast(message)
    }

    private fun successMessage(context: Context, currentValue: String): String {
        return context.getString(R.string.playlist_x_renamed_to_y, itemTitle, currentValue)
    }
}