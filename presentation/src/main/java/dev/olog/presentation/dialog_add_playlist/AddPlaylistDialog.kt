package dev.olog.presentation.dialog_add_playlist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseDialogFragment
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.utils.makeDialog
import dev.olog.presentation.utils.withArguments
import javax.inject.Inject

class AddPlaylistDialog : BaseDialogFragment() {

    companion object {
        const val TAG = "AddPlaylistDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: String): AddPlaylistDialog {
            return AddPlaylistDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }

    @Inject lateinit var presenter: AddPlaylistPresenter
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var mediaId: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.popup_add_to_playlist)
                .setItems(createItems(), { _, which -> presenter.onItemClick(which) })
                .setPositiveButton(R.string.popup_new_playlist, { _, _ ->
                    navigator.toCreatePlaylistDialog(mediaId)
                })

        return builder.makeDialog()
    }

    private fun createItems(): Array<out CharSequence> {
        val playlistsAsList = presenter.getPlaylistsAsList().map { "- ${it.playlistTitle}" }
        if (playlistsAsList.isEmpty()){
            return arrayOf(context!!.getString(R.string.popup_no_playlist))
        }
        return playlistsAsList.toTypedArray()
    }

}