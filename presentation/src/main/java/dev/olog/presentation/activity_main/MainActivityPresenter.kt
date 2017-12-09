package dev.olog.presentation.activity_main

import android.app.Activity
import dev.olog.domain.interactor.floating_info.SetFloatingInfoRequestUseCase
import dev.olog.presentation.service_floating_info.FloatingInfoServiceBinder
import dev.olog.presentation.service_floating_info.FloatingInfoServiceHelper
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
        private val setFloatingInfoRequestUseCase: SetFloatingInfoRequestUseCase,
        private val floatingInfoClass: FloatingInfoServiceBinder
) {

    fun startFloatingService(activity: Activity, songTitle: String?){
        songTitle?.let { setFloatingInfoRequestUseCase.execute(it) }

        FloatingInfoServiceHelper.startService(activity, floatingInfoClass)
    }

}