package dev.olog.presentation.dialogs.delete

import android.app.RecoverableSecurityException
import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.NavigationUtils
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.presentation.utils.asHtml
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.android.utils.isQ
import dev.olog.shared.lazyFast
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DeleteDialog: BaseDialog() {

    companion object {
        const val TAG = "DeleteDialog"

        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): DeleteDialog {
            return DeleteDialog().withArguments(
                NavigationUtils.ARGUMENTS_MEDIA_ID to mediaId.toString(),
                NavigationUtils.ARGUMENTS_LIST_SIZE to listSize,
                NavigationUtils.ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId: MediaId by lazyFast {
        val mediaId = arguments!!.getString(NavigationUtils.ARGUMENTS_MEDIA_ID)!!
        MediaId.fromString(mediaId)
    }
    private val title: String by lazyFast { arguments!!.getString(NavigationUtils.ARGUMENTS_ITEM_TITLE)!! }
    private val listSize: Int by lazyFast { arguments!!.getInt(NavigationUtils.ARGUMENTS_LIST_SIZE) }

    @Inject lateinit var presenter: DeleteDialogPresenter

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_delete)
            .setMessage(createMessage().asHtml())
            .setPositiveButton(R.string.popup_positive_delete, null)
            .setNegativeButton(R.string.popup_negative_no, null)
    }

    override fun positionButtonAction(context: Context) {
        lifecycleScope.launch {
            catchRecoverableSecurityException(this@DeleteDialog) {
                tryExecute()
            }
        }
    }

    private suspend fun tryExecute(){
        var message: String
        try {
            presenter.execute(mediaId)
            message = successMessage(act)
        } catch (ex: Throwable) {
            if (isQ() && ex is RecoverableSecurityException){
                throw ex
            }
            ex.printStackTrace()
            message = failMessage(act)
        }
        act.toast(message)
        dismiss()
    }

    override suspend fun onRecoverableSecurityExceptionRecovered() {
        tryExecute()
    }

    private fun successMessage(context: Context): String {
        return when (mediaId.category) {
            MediaIdCategory.PLAYLISTS -> context.getString(R.string.playlist_x_deleted, title)
            MediaIdCategory.SONGS -> context.getString(R.string.song_x_deleted, title)
            else -> context.resources.getQuantityString(
                R.plurals.xx_songs_deleted_from_y,
                listSize, listSize, title
            )
        }
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage() : String {
        val itemTitle = arguments!!.getString(NavigationUtils.ARGUMENTS_ITEM_TITLE)

        return when {
            mediaId.isAll || mediaId.isLeaf -> getString(R.string.delete_song_y, itemTitle)
            mediaId.isPlaylist -> getString(R.string.delete_playlist_y, itemTitle)
            else -> context!!.resources.getQuantityString(R.plurals.delete_xx_songs_from_y, listSize, listSize)
        }
    }

}