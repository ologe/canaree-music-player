package dev.olog.presentation.dialog_rename

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.animation.AnimationUtils
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseDialogFragment
import dev.olog.presentation.utils.ImeUtils
import dev.olog.presentation.utils.extension.makeDialog
import dev.olog.presentation.utils.extension.withArguments
import javax.inject.Inject

class RenameDialog : BaseDialogFragment() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: String, itemTitle: String): RenameDialog {
            return RenameDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject lateinit var presenter: RenameDialogPresenter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.popup_rename)
                .setView(R.layout.layout_edit_text)
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_rename, null)

        val dialog = builder.makeDialog()
        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)
        editText.setText(arguments!!.getString(ARGUMENTS_ITEM_TITLE))

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val playlistTitle = editText.text.toString()
            val errorMessage = presenter.checkData(playlistTitle)
            when (errorMessage) {
                R.string.popup_playlist_name_not_valid,
                R.string.popup_playlist_name_already_exist -> {
                    val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
                    editText.startAnimation(shake)
                    editTextLayout.error = getString(errorMessage)
                    editTextLayout.isErrorEnabled = true
                    editTextLayout.postDelayed({ if (editTextLayout != null) editTextLayout.isErrorEnabled = false }, (2 * 1000).toLong())
                }
                else -> {
                    presenter.execute(playlistTitle)
                            .subscribe({}, Throwable::printStackTrace)
                    dismiss()
                }
            }
        }

        ImeUtils.showIme(editText) // todo not working

        return dialog
    }

}