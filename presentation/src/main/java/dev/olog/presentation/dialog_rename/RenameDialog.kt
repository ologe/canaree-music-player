package dev.olog.presentation.dialog_rename

import dev.olog.presentation._base.BaseDialogFragment
import dev.olog.presentation.utils.withArguments

class RenameDialog : BaseDialogFragment() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: String): RenameDialog {
            return RenameDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }

}