package dev.olog.shared_android.analitycs

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object FirebaseAnalytics {

    private const val PLAYER_PANEL_STATE = "PLAYER_STATE"
    private const val PLAYER_PANEL_STATE_VISIBILITY = "PLAYER_PANEL_STATE_VISIBILITY"

    private const val FLOATING_LIFE = "FLOATING_LIFE"
    private const val FLOATING_LIFE_MILLIS = "FLOATING_LIFE_MILLIS"

    private const val NEURAL_SUCCESS = "NEURAL_SUCCESS"
    private const val NEURAL_SUCCESS_VALUE = "NEURAL_SUCCESS_VALUE"

    private var INSTANCE : FirebaseAnalytics? = null

    fun initialize(context: Context){
        INSTANCE = FirebaseAnalytics.getInstance(context)
    }

    fun trackFragment(activity: Activity, fragmentTag: String){
        INSTANCE?.setCurrentScreen(activity, fragmentTag, fragmentTag)
    }

    fun onPlayerVisibilityChanged(visible: Boolean){
        val bundle = Bundle()
        bundle.putBoolean(PLAYER_PANEL_STATE_VISIBILITY, visible)
        INSTANCE?.logEvent(PLAYER_PANEL_STATE, bundle)
    }

    fun trackFloatingServiceLife(time: String){
        val bundle = Bundle()
        bundle.putString(FLOATING_LIFE_MILLIS, time)
        INSTANCE?.logEvent(FLOATING_LIFE, bundle)
    }

    fun trackNeuralSuccess(success: Boolean){
        val bundle = Bundle()
        bundle.putBoolean(NEURAL_SUCCESS_VALUE, success)
        INSTANCE?.logEvent(NEURAL_SUCCESS, bundle)
    }

}