package dev.olog.feature.dialog.favorite

import androidx.core.text.parseAsHtml
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.dialog.R
import dev.olog.feature.dialog.base.BaseDialog
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.launch
import dev.olog.shared.android.extensions.toast

@AndroidEntryPoint
class AddFavoriteDialog : BaseDialog() {

    private val mediaId by argument(Params.MEDIA_ID, MediaId::fromString)
    private val title by argument<String>(Params.TITLE)
    private val listSize by argument<Int>(Params.SIZE)

    private val viewModel by viewModels<AddFavoriteDialogViewModel>()

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_add_to_favorites)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_ok, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    override fun positionButtonAction() {
        launch {
            var message: String
            try {
                viewModel.execute()
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
        return when (mediaId) {
            is MediaId.Category -> resources.getQuantityString(R.plurals.xx_songs_added_to_favorites, listSize, listSize)
            is MediaId.Track -> getString(R.string.song_x_added_to_favorites, title)
        }
    }

    private fun failMessage(): String {
        return getString(R.string.popup_error_message)
    }

    private fun createMessage() : String {
        return when (mediaId) {
            is MediaId.Category -> resources.getQuantityString(R.plurals.add_xx_songs_to_favorite, listSize, listSize)
            is MediaId.Track -> getString(R.string.add_song_x_to_favorite, title)
        }
    }

}