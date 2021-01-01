package dev.olog.feature.dialog.playlist.duplicates

import androidx.core.text.parseAsHtml
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.dialog.R
import dev.olog.feature.dialog.base.BaseDialog
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.launch
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments
import javax.inject.Inject

@AndroidEntryPoint
class RemovePlaylistDuplicatesDialog: BaseDialog() {

    companion object {
        const val TAG = "RemoveDuplicatesDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: MediaId, itemTitle: String): RemovePlaylistDuplicatesDialog {
            return RemovePlaylistDuplicatesDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject lateinit var presenter: RemovePlaylistDuplicatesDialogPresenter


    private val mediaId by argument(ARGUMENTS_MEDIA_ID, MediaId::fromString)
    private val itemTitle by argument<String>(ARGUMENTS_ITEM_TITLE)

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.remove_duplicates_title)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_remove, null)
            .setNegativeButton(R.string.popup_negative_no, null)
    }

    override fun positionButtonAction() {
        launch {
            var message: String
            try {
                presenter.execute(mediaId)
                message = successMessage()
            } catch (ex: Throwable) {
                ex.printStackTrace()
                message = failMessage()
            }
            requireActivity().toast(message)
            dismiss()

        }
    }

    private fun successMessage(): String {
        return getString(R.string.remove_duplicates_success, itemTitle)
    }

    private fun failMessage(): String {
        return getString(R.string.popup_error_message)
    }

    private fun createMessage() : String {
        return getString(R.string.remove_duplicates_message, itemTitle)
    }

}