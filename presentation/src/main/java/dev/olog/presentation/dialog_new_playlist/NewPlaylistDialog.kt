package dev.olog.presentation.dialog_new_playlist

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
import dev.olog.shared.MediaId
import dev.olog.shared.unsubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewPlaylistDialog : BaseDialogFragment() {

    companion object {
        const val TAG = "NewPlaylistDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: MediaId): NewPlaylistDialog {
            return NewPlaylistDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    private var disposable : Disposable? = null

    @Inject lateinit var presenter: NewPlaylistDialogPresenter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.popup_new_playlist)
                .setView(R.layout.layout_edit_text)
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_create, null)

        val dialog = builder.makeDialog()
        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)

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

        disposable = Observable.timer(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ ImeUtils.showIme(editText) }, Throwable::printStackTrace)

        return dialog
    }

    override fun onStop() {
        super.onStop()
        disposable.unsubscribe()
    }

}