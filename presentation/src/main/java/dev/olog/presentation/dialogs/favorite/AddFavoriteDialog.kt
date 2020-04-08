package dev.olog.presentation.dialogs.favorite

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.presentation.utils.asHtml
import dev.olog.shared.android.extensions.getArgument
import dev.olog.shared.android.extensions.launchWhenResumed
import dev.olog.feature.presentation.base.extensions.toast
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import timber.log.Timber
import javax.inject.Inject

class AddFavoriteDialog : BaseDialog() {

    companion object {
        const val TAG = "AddFavoriteDialog"
        const val ARGUMENTS_MEDIA_ID = "${TAG}_arguments_media_id"
        const val ARGUMENTS_LIST_SIZE = "${TAG}_arguments_list_size"
        const val ARGUMENTS_ITEM_TITLE = "${TAG}_arguments_item_title"

        @JvmStatic
        fun newInstance(mediaId: PresentationId, listSize: Int, itemTitle: String): AddFavoriteDialog {
            return AddFavoriteDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId by lazyFast {
        getArgument<PresentationId>(ARGUMENTS_MEDIA_ID)
    }
    private val title by lazyFast { getArgument<String>(ARGUMENTS_ITEM_TITLE) }
    private val listSize by lazyFast { getArgument<Int>(ARGUMENTS_LIST_SIZE) }

    @Inject lateinit var presenter: AddFavoriteDialogPresenter

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_add_to_favorites)
            .setMessage(createMessage().asHtml())
            .setPositiveButton(R.string.popup_positive_ok, null)
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
        return when (mediaId) {
            is PresentationId.Track -> context.getString(R.string.song_x_added_to_favorites, title)
            is PresentationId.Category -> context.resources.getQuantityString(R.plurals.xx_songs_added_to_favorites, listSize, listSize)
        }
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage() : String {
        return when (mediaId) {
            is PresentationId.Track -> getString(R.string.add_song_x_to_favorite, title)
            is PresentationId.Category -> requireContext().resources.getQuantityString(
                R.plurals.add_xx_songs_to_favorite, listSize, listSize)
        }
    }

}