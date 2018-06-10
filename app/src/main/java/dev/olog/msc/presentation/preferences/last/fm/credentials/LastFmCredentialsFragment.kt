package dev.olog.msc.presentation.preferences.last.fm.credentials

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import dev.olog.msc.R
import dev.olog.msc.domain.entity.UserCredentials
import dev.olog.msc.domain.interactor.last.fm.scrobble.GetLastFmUserCredentials
import dev.olog.msc.domain.interactor.last.fm.scrobble.UpdateLastFmUserCredentials
import dev.olog.msc.presentation.base.BaseDialogFragment
import dev.olog.msc.presentation.theme.ThemedDialog
import dev.olog.msc.utils.k.extension.ctx
import javax.inject.Inject

class LastFmCredentialsFragment : BaseDialogFragment() {

    companion object {
        const val TAG = "LastFmCredentialsFragment"

        @JvmStatic
        fun newInstance(): LastFmCredentialsFragment {
            return LastFmCredentialsFragment()
        }
    }

    @Inject lateinit var getLastFmUserCredentials: GetLastFmUserCredentials
    @Inject lateinit var updateLastFmUserCredentials: UpdateLastFmUserCredentials

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view : View = inflater.inflate(R.layout.layout_credentials_edit_text, null, false)

        val builder = ThemedDialog.builder(ctx)
                .setTitle(R.string.prefs_last_fm_credentials_title)
                .setMessage(R.string.prefs_last_fm_credentials_message)
                .setView(view)
                .setPositiveButton(R.string.credentials_button_positive, null)
                .setNegativeButton(R.string.credentials_button_negative, null)

        val userName = view.findViewById<EditText>(R.id.username)
        val password = view.findViewById<EditText>(R.id.password)

        val credentials = getLastFmUserCredentials.execute()
        userName.setText(credentials.username)
        password.setText(credentials.password)

        val dialog = builder.show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val user = UserCredentials(
                    userName.text.toString(),
                    password.text.toString()
            )
            val disp = updateLastFmUserCredentials.execute(user)
                    .subscribe({ dismiss() }, Throwable::printStackTrace)
        }

        return dialog
    }

}