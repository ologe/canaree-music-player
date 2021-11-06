package dev.olog.feature.dialogs.rate

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.core.PreferenceManager
import dev.olog.shared.android.utils.PlayStoreUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RateAppDialog @Inject constructor(
    private val activity: FragmentActivity,
    preferenceManager: PreferenceManager,
) : DefaultLifecycleObserver {

    companion object {
        private var counterAlreadyIncreased = false

        private const val PREFS_APP_STARTED_COUNT = "prefs.app.started.count"
        private const val PREFS_APP_RATE_NEVER_SHOW_AGAIN = "prefs.app.rate.never.show"
    }

    private val countPref = preferenceManager.create(PREFS_APP_STARTED_COUNT, 0)
    private val neverShowPref = preferenceManager.create(PREFS_APP_RATE_NEVER_SHOW_AGAIN, false)

    private var disposable: Job? = null

    init {
        activity.lifecycle.addObserver(this)
        check(activity)
    }

    private fun check(activity: FragmentActivity) {
        disposable = activity.lifecycleScope.launchWhenResumed {
            val show = updateCounter()
            delay(2000)
            if (show) {
                showAlert()
            }
        }
    }

    private suspend fun showAlert() = withContext(Dispatchers.Main) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(localization.R.string.rate_app_title)
            .setMessage(localization.R.string.rate_app_message)
            .setPositiveButton(localization.R.string.rate_app_positive_button) { _, _ ->
                setNeverShowAgain()
                PlayStoreUtils.open(activity)
            }
            .setNegativeButton(localization.R.string.rate_app_negative_button) { _, _ -> setNeverShowAgain() }
            .setNeutralButton(localization.R.string.rate_app_neutral_button) { _, _ -> }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable?.cancel()
    }

    /**
     * @return true when is requested to show rate dialog
     */
    private suspend fun updateCounter(): Boolean = withContext(Dispatchers.IO) {
        if (!counterAlreadyIncreased) {
            counterAlreadyIncreased = true

            val oldValue = countPref.get()
            val newValue = oldValue + 1
            countPref.set(newValue)
            newValue.rem(20) == 0 && !isNeverShowAgain()
        } else {
            false
        }
    }

    private fun isNeverShowAgain(): Boolean {
        return neverShowPref.get()
    }

    private fun setNeverShowAgain() {
        return neverShowPref.set(true)
    }

}