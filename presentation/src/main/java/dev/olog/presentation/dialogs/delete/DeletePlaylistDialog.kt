package dev.olog.presentation.dialogs.delete

import android.app.RecoverableSecurityException
import android.content.Context
import androidx.core.text.parseAsHtml
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.platform.BuildVersion
import dev.olog.platform.extension.act
import dev.olog.platform.extension.toast
import dev.olog.platform.extension.withArguments
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.shared.lazyFast
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeletePlaylistDialog: BaseDialog() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: MediaId, title: String): DeletePlaylistDialog {
            return DeletePlaylistDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_ITEM_TITLE to title
            )
        }
    }

    private val mediaId: MediaId by lazyFast {
        val mediaId = arguments!!.getString(ARGUMENTS_MEDIA_ID)!!
        MediaId.fromString(mediaId)
    }
    private val title: String by lazyFast { arguments!!.getString(ARGUMENTS_ITEM_TITLE)!! }

    private val viewModel by viewModels<DeletePlaylistDialogViewModel>()

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_delete)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_delete, null)
            .setNegativeButton(R.string.popup_negative_no, null)
    }

    override fun positionButtonAction(context: Context) {
        lifecycleScope.launch {
            deletePlaylist()
        }
    }

    private suspend fun deletePlaylist(){
        var message: String
        try {
            viewModel.execute(mediaId)
            message = successMessage(act)
        } catch (ex: Throwable) {
            if (BuildVersion.isQ() && ex is RecoverableSecurityException){
                throw ex
            }
            ex.printStackTrace()
            message = failMessage(act)
        }
        act.toast(message)
        dismiss()
    }

    override suspend fun onRecoverableSecurityExceptionRecovered() {
        deletePlaylist()
    }

    private fun successMessage(context: Context): String {
        return context.getString(R.string.playlist_x_deleted, title)
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage() : String {
        val itemTitle = arguments!!.getString(ARGUMENTS_ITEM_TITLE)
        return getString(R.string.delete_playlist_y, itemTitle)
    }

}