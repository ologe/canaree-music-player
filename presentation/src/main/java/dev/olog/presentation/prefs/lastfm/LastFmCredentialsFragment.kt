package dev.olog.presentation.prefs.lastfm

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import dev.olog.core.entity.UserCredentials
import dev.olog.core.interactor.lastfm.GetLastFmUserCredentials
import dev.olog.core.interactor.lastfm.UpdateLastFmUserCredentials
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseDialogFragment
import dev.olog.shared.android.extensions.ctx
import javax.inject.Inject

class LastFmCredentialsFragment : BaseDialogFragment() {

    companion object {
        const val TAG = "LastFmCredentialsFragment"

        @JvmStatic
        fun newInstance(): LastFmCredentialsFragment {
            return LastFmCredentialsFragment()
        }
    }

    @Inject
    lateinit var getLastFmUserCredentials: GetLastFmUserCredentials
    @Inject
    lateinit var updateLastFmUserCredentials: UpdateLastFmUserCredentials

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view: View = inflater.inflate(R.layout.fragment_credentials, null, false)

        val builder = AlertDialog.Builder(ctx)
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
            TODO()
//            val disp = updateLastFmUserCredentials(user)
//                .subscribe({ dismiss() }, Throwable::printStackTrace)
        }

        return dialog
    }

}