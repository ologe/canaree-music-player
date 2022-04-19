package dev.olog.presentation.dialogs.playlist.duplicates

import android.content.Context
import androidx.core.text.parseAsHtml
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.launchWhenResumed
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments

@AndroidEntryPoint
class RemoveDuplicatesDialog: BaseDialog() {

    companion object {
        const val TAG = "RemoveDuplicatesDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        @JvmStatic
        fun newInstance(mediaId: MediaId, itemTitle: String): RemoveDuplicatesDialog {
            return RemoveDuplicatesDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val viewModel by viewModels<RemoveDuplicatesDialogViewModel>()


    private val mediaId by argument<MediaId>(ARGUMENTS_MEDIA_ID)
    private val itemTitle by argument<String>(ARGUMENTS_ITEM_TITLE)

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.remove_duplicates_title)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_remove, null)
            .setNegativeButton(R.string.popup_negative_no, null)
    }

    override fun positionButtonAction(context: Context) {
        launchWhenResumed {
            var message: String
            try {
                viewModel.execute(mediaId)
                message = successMessage(requireContext())
            } catch (ex: Throwable) {
                ex.printStackTrace()
                message = failMessage(requireContext())
            }
            toast(message)
            dismiss()

        }
    }

    private fun successMessage(context: Context): String {
        return context.getString(R.string.remove_duplicates_success, itemTitle)
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage() : String {
        return context!!.getString(R.string.remove_duplicates_message, itemTitle)
    }

}