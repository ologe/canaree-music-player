package dev.olog.presentation.rateapp

import android.content.Context
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.shared.ApplicationContext
import dev.olog.presentation.R
import dev.olog.shared.android.utils.PlayStoreUtils
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.*
import javax.inject.Inject

private var counterAlreadyIncreased = false

private const val PREFS_APP_STARTED_COUNT = "prefs.app.started.count"
private const val PREFS_APP_RATE_NEVER_SHOW_AGAIN = "prefs.app.rate.never.show"

// TODO check implementation
class RateAppDialog @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activity: FragmentActivity

) : DefaultLifecycleObserver {

    private var job by autoDisposeJob()

    init {
        activity.lifecycle.addObserver(this)
        check(activity)
    }

    private fun check(activity: FragmentActivity) {
        job = activity.lifecycleScope.launchWhenResumed {
            val show = updateCounter(activity)
            delay(2000)
            if (show) {
                showAlert()
            }
        }
    }

    private suspend fun showAlert() = withContext(Dispatchers.Main) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.rate_app_title)
            .setMessage(R.string.rate_app_message)
            .setPositiveButton(R.string.rate_app_positive_button) { _, _ ->
                setNeverShowAgain()
                PlayStoreUtils.open(activity)
            }
            .setNegativeButton(R.string.rate_app_negative_button) { _, _ -> setNeverShowAgain() }
            .setNeutralButton(R.string.rate_app_neutral_button) { _, _ -> }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        job = null
    }

    /**
     * @return true when is requested to show rate dialog
     */
    private suspend fun updateCounter(context: Context): Boolean = withContext(Dispatchers.IO) {
        if (!counterAlreadyIncreased) {
            counterAlreadyIncreased = true

            val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

            val oldValue = prefs.getInt(PREFS_APP_STARTED_COUNT, 0)
            val newValue = oldValue + 1
            prefs.edit { putInt(PREFS_APP_STARTED_COUNT, newValue) }

            newValue.rem(20) == 0 && !isNeverShowAgain()
        } else {
            false
        }
    }

    private fun isNeverShowAgain(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(PREFS_APP_RATE_NEVER_SHOW_AGAIN, false)
    }

    private fun setNeverShowAgain() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit { putBoolean(PREFS_APP_RATE_NEVER_SHOW_AGAIN, true) }
    }

}