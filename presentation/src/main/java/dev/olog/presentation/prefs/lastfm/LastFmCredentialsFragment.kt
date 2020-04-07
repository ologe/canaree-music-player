@file:Suppress("DEPRECATION")

package dev.olog.presentation.prefs.lastfm

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.umass.lastfm.Authenticator
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.domain.entity.UserCredentials
import dev.olog.domain.interactor.lastfm.GetLastFmUserCredentials
import dev.olog.domain.interactor.lastfm.UpdateLastFmUserCredentials
import dev.olog.presentation.BuildConfig
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseDialogFragment
import dev.olog.shared.android.extensions.launchWhenResumed
import dev.olog.shared.android.extensions.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
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

    private var loader: ProgressDialog? = null

    private var job by autoDisposeJob()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireActivity())
        val view: View = inflater.inflate(R.layout.fragment_credentials, null, false)

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.prefs_last_fm_credentials_title)
            .setMessage(R.string.prefs_last_fm_credentials_message)
            .setView(view)
            .setPositiveButton(R.string.credentials_button_positive, null)
            .setNegativeButton(R.string.credentials_button_negative, null)

        val userName = view.findViewById<EditText>(R.id.username)
        val password = view.findViewById<EditText>(R.id.password)

        val credentials = getLastFmUserCredentials()
        userName.setText(credentials.username)
        password.setText(credentials.password)

        val dialog = builder.show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            job = launchWhenResumed {

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
                } catch (ex: Exception) {
                    Timber.e(ex)
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
                    job = null
                    loader = null
                }
            }
        }
    }

    @SuppressLint("ConcreteDispatcherIssue")
    private suspend fun tryAuthenticate(user: UserCredentials): Boolean =
        withContext(Dispatchers.IO) {
            Authenticator.getMobileSession(
                user.username,
                user.password,
                BuildConfig.LAST_FM_KEY,
                BuildConfig.LAST_FM_SECRET
            ) != null

        }

    private fun onSuccess(user: UserCredentials) {
        updateLastFmUserCredentials(user)
        requireContext().toast("Success")
        dismiss()
    }

    private fun onFail() {
        requireContext().toast("Failed")
    }

}