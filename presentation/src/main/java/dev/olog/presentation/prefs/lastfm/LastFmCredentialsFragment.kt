@file:Suppress("DEPRECATION")

package dev.olog.presentation.prefs.lastfm

import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import de.umass.lastfm.Authenticator
import dev.olog.core.Config
import dev.olog.core.entity.UserCredentials
import dev.olog.core.interactor.lastfm.GetLastFmUserCredentials
import dev.olog.core.interactor.lastfm.UpdateLastFmUserCredentials
import dev.olog.presentation.R
import dev.olog.platform.extension.ctx
import dev.olog.platform.extension.toast
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class LastFmCredentialsFragment : DialogFragment() {

    companion object {
        const val TAG = "LastFmCredentialsFragment"

        fun newInstance(): LastFmCredentialsFragment {
            return LastFmCredentialsFragment()
        }
    }

    @Inject
    lateinit var getLastFmUserCredentials: GetLastFmUserCredentials
    @Inject
    lateinit var updateLastFmUserCredentials: UpdateLastFmUserCredentials
    @Inject
    lateinit var config: Config

    private var loader: ProgressDialog? = null

    private var job: Job? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view: View = inflater.inflate(R.layout.fragment_credentials, null, false)

        val builder = MaterialAlertDialogBuilder(ctx)
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
            job = viewLifecycleOwner.lifecycleScope.launch {

                val user = UserCredentials(
                    userName.text.toString(),
                    password.text.toString()
                )
                showLoader()
                try {
                    if (tryAuthenticate(user)) {
                        onSuccess(user)
                    } else {
                        onFail()
                    }
                } catch (ex: Throwable) {
                    onFail()
                } finally {
                    loader?.dismiss()
                }
            }

        }

        return dialog
    }

    private fun showLoader() {
        loader = ProgressDialog.show(context, "", "Authenticating", true).apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            setOnCancelListener {
                setOnCancelListener {
                    job?.cancel()
                    loader = null
                }
            }
        }
    }

    private suspend fun tryAuthenticate(user: UserCredentials): Boolean =
        withContext(Dispatchers.IO) {
            Authenticator.getMobileSession(
                user.username,
                user.password,
                config.lastFmKey,
                config.lastFmSecret
            ) != null

        }

    private fun onSuccess(user: UserCredentials) {
        updateLastFmUserCredentials(user)
        ctx.toast("Success")
        dismiss()
    }

    private fun onFail() {
        ctx.toast("Failed")
    }

}