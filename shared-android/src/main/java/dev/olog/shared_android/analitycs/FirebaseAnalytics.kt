package dev.olog.shared_android.analitycs

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

object FirebaseAnalytics {

    private const val PLAYER_PANEL_STATE = "player_state"
    private const val PLAYER_PANEL_STATE_VISIBILITY = "player_state_visible"

    private const val FLOATING_LIFE = "floating_life"
    private const val FLOATING_LIFE_MILLIS = "floating_life_time"

    private const val NEURAL_START = "neural_start"

    private const val NEURAL_SUCCESS = "neural_end"
    private const val NEURAL_SUCCESS_VALUE = "neural_end_value"

    private var INSTANCE : FirebaseAnalytics? = null

    fun initialize(context: Context){
        INSTANCE = FirebaseAnalytics.getInstance(context)
    }

    fun trackFragment(activity: Activity, fragmentTag: String){
        wrapInCompletable {
            INSTANCE?.setCurrentScreen(activity, fragmentTag, fragmentTag)
        }
    }

    fun onPlayerVisibilityChanged(visible: Boolean){
        wrapInCompletable {
            val bundle = Bundle()
            bundle.putBoolean(PLAYER_PANEL_STATE_VISIBILITY, visible)
            INSTANCE?.logEvent(PLAYER_PANEL_STATE, bundle)
        }
    }

    fun trackFloatingServiceLife(time: String){
        wrapInCompletable {
            val bundle = Bundle()
            bundle.putString(FLOATING_LIFE_MILLIS, time)
            INSTANCE?.logEvent(FLOATING_LIFE, bundle)
        }
    }

    fun trackNeuralSuccess(success: Boolean){
        wrapInCompletable {
            val bundle = Bundle()
            bundle.putBoolean(NEURAL_SUCCESS_VALUE, success)
            INSTANCE?.logEvent(NEURAL_SUCCESS, bundle)
        }
    }

    fun trackNeuralStart(){
        wrapInCompletable {
            val bundle = Bundle()
            bundle.putBoolean(NEURAL_START, true)
            INSTANCE?.logEvent(NEURAL_START, bundle)
        }
    }

    private fun wrapInCompletable(func: () -> Unit){
        Completable.fromCallable { func() }
                .subscribeOn(Schedulers.io())
                .subscribe({}, Throwable::printStackTrace)
    }

}