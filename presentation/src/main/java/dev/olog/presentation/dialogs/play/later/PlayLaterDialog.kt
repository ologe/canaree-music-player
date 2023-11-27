package dev.olog.presentation.dialogs.play.later

import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.presentation.NavigationUtils
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.presentation.utils.asHtml
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlayLaterDialog : BaseDialog() {

    companion object {
        const val TAG = "PlayLaterDialog"

        @JvmStatic
        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): PlayLaterDialog {
            return PlayLaterDialog().withArguments(
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

    @Inject lateinit var presenter: PlayLaterDialogPresenter

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_play_later)
            .setMessage(createMessage().asHtml())
            .setPositiveButton(R.string.popup_positive_ok, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    private fun successMessage(context: Context): String {
        return if (mediaId.isLeaf){
            context.getString(R.string.song_x_added_to_play_later, title)
        } else context.resources.getQuantityString(R.plurals.xx_songs_added_to_play_later, listSize, listSize)
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    override fun positionButtonAction(context: Context) {
        launch {
            var message: String
            try {
                val mediaController = MediaControllerCompat.getMediaController(act)
                presenter.execute(mediaController, mediaId)
                message = successMessage(act)
            } catch (ex: Throwable) {
                ex.printStackTrace()
                message = failMessage(act)
            }
            act.toast(message)
            dismiss()
        }
    }

    private fun createMessage() : String {
        if (mediaId.isAll || mediaId.isLeaf){
            return getString(R.string.add_song_x_to_play_later, title)
        }
        return context!!.resources.getQuantityString(R.plurals.add_xx_songs_to_play_later, listSize, listSize)
    }

}