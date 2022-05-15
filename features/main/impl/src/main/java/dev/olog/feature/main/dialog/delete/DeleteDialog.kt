package dev.olog.feature.main.dialog.delete

import android.app.RecoverableSecurityException
import android.content.Context
import androidx.core.text.parseAsHtml
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.platform.fragment.BaseDialog
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.shared.extension.argument
import dev.olog.shared.extension.launchWhenResumed
import dev.olog.shared.extension.toast
import dev.olog.shared.extension.withArguments
import dev.olog.shared.isQ

@AndroidEntryPoint
class DeleteDialog: BaseDialog() {

    companion object {
        val TAG = FragmentTagFactory.create(DeleteDialog::class)
        val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        val ARGUMENTS_LIST_SIZE = "$TAG.arguments.list_size"
        val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): DeleteDialog {
            return DeleteDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId by argument<MediaId>(ARGUMENTS_MEDIA_ID)
    private val title by argument<String>(ARGUMENTS_ITEM_TITLE)
    private val listSize by argument<Int>(ARGUMENTS_LIST_SIZE)

    private val viewModel by viewModels<DeleteDialogViewModel>()

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(localization.R.string.popup_delete)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(localization.R.string.popup_positive_delete, null)
            .setNegativeButton(localization.R.string.popup_negative_no, null)
    }

    override fun positionButtonAction(context: Context) {
        launchWhenResumed {
            catchRecoverableSecurityException(this@DeleteDialog) {
                tryExecute()
            }
        }
    }

    private suspend fun tryExecute(){
        var message: String
        try {
            viewModel.execute(mediaId)
            message = successMessage(requireContext())
        } catch (ex: Throwable) {
            if (isQ() && ex is RecoverableSecurityException){
                throw ex
            }
            ex.printStackTrace()
            message = failMessage(requireContext())
        }
        toast(message)
        dismiss()
    }

    override suspend fun onRecoverableSecurityExceptionRecovered() {
        tryExecute()
    }

    private fun successMessage(context: Context): String {
        return when (mediaId.category) {
            MediaIdCategory.PLAYLISTS -> context.getString(localization.R.string.playlist_x_deleted, title)
            MediaIdCategory.SONGS -> context.getString(localization.R.string.song_x_deleted, title)
            else -> context.resources.getQuantityString(
                localization.R.plurals.xx_songs_deleted_from_y,
                listSize, listSize, title
            )
        }
    }

    private fun failMessage(context: Context): String {
        return context.getString(localization.R.string.popup_error_message)
    }

    private fun createMessage() : String {
        val itemTitle = arguments!!.getString(ARGUMENTS_ITEM_TITLE)

        return when {
            mediaId.isAll || mediaId.isLeaf -> getString(localization.R.string.delete_song_y, itemTitle)
            mediaId.isPlaylist -> getString(localization.R.string.delete_playlist_y, itemTitle)
            else -> context!!.resources.getQuantityString(localization.R.plurals.delete_xx_songs_from_y, listSize, listSize)
        }
    }

}