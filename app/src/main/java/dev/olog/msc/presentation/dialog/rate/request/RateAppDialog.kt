package dev.olog.msc.presentation.dialog.rate.request

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import dev.olog.msc.R
import dev.olog.msc.presentation.utils.openPlayStore
import dev.olog.presentation.dagger.ActivityLifecycle
import dev.olog.shared.extensions.unsubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private var counterAlreadyIncreased = false

private const val PREFS_APP_STARTED_COUNT = "prefs.app.started.count"
private const val PREFS_APP_RATE_NEVER_SHOW_AGAIN = "prefs.app.rate.never.show"

class RateAppDialog @Inject constructor(
        private val activity: Activity,
        @ActivityLifecycle private val lifecycle: Lifecycle

): DefaultLifecycleObserver {

    private var disposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
        check()
    }

    private fun check(){
        disposable = updateCounter(activity)
                .delay(2, TimeUnit.SECONDS, Schedulers.computation())
                .filter { it }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    AlertDialog.Builder(activity)
                            .setTitle(R.string.rate_app_title)
                            .setMessage(R.string.rate_app_message)
                            .setPositiveButton(R.string.rate_app_positive_button) { _, _ ->
                                setNeverShowAgain()
                                openPlayStore(activity)
                            }
                            .setNegativeButton(R.string.rate_app_negative_button) { _, _ -> setNeverShowAgain() }
                            .setNeutralButton(R.string.rate_app_neutral_button) { _, _ ->  }
                            .setCancelable(false)
                            .show()

                }, Throwable::printStackTrace)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

    /**
     * @return true when is requested to show rate dialog
     */
    private fun updateCounter(context: Context): Single<Boolean> {
        return Single.fromCallable {
            if (!counterAlreadyIncreased){
                counterAlreadyIncreased = true

                val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

                val oldValue = prefs.getInt(PREFS_APP_STARTED_COUNT, 0)
                val newValue = oldValue + 1
                prefs.edit { putInt(PREFS_APP_STARTED_COUNT, newValue) }

                newValue.rem(20) == 0 && !isNeverShowAgain()
            } else {
                false
            }


        }.subscribeOn(Schedulers.io())
    }

    private fun isNeverShowAgain(): Boolean{
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        return prefs.getBoolean(PREFS_APP_RATE_NEVER_SHOW_AGAIN, false)
    }

    private fun setNeverShowAgain(){
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        prefs.edit { putBoolean(PREFS_APP_RATE_NEVER_SHOW_AGAIN, true) }
    }

}