package dev.olog.presentation.dialogs.playlist.clear

import android.content.Context
import androidx.core.text.parseAsHtml
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.MediaId
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.feature.presentation.base.extensions.getArgument
import dev.olog.feature.presentation.base.extensions.launchWhenResumed
import dev.olog.feature.presentation.base.extensions.toast
import dev.olog.feature.presentation.base.extensions.withArguments
import dev.olog.feature.presentation.base.model.toPresentation
import dev.olog.shared.lazyFast
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ClearPlaylistDialog : BaseDialog() {

    companion object {
        const val TAG = "ClearPlaylistDialog"
        const val ARGUMENTS_MEDIA_ID = "${TAG}_arguments_media_id"
        const val ARGUMENTS_ITEM_TITLE = "${TAG}_arguments_item_title"

        @JvmStatic
        fun newInstance(mediaId: MediaId.Category, itemTitle: String): ClearPlaylistDialog {
            return ClearPlaylistDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toPresentation(),
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId by lazyFast {
        getArgument<PresentationId.Category>(ARGUMENTS_MEDIA_ID)
    }
    private val title by lazy { getArgument<String>(ARGUMENTS_ITEM_TITLE) }

    @Inject lateinit var presenter: ClearPlaylistDialogPresenter

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_clear_playlist)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_delete, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    override fun positionButtonAction(context: Context) {
        launchWhenResumed {
            var message: String
            try {
                presenter.execute(mediaId)
                message = successMessage(requireActivity())
            } catch (ex: Exception) {
                Timber.e(ex)
                message = failMessage(requireActivity())
            }
            requireActivity().toast(message)
            dismiss()

        }
    }

    private fun successMessage(context: Context): String {
        return context.getString(R.string.playlist_x_cleared, title)
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage() : String {
        return requireContext().getString(R.string.remove_songs_from_playlist_y, title)
    }

}