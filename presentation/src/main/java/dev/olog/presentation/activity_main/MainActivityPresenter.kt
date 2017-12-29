package dev.olog.presentation.activity_main

import android.app.Activity
import dev.olog.domain.interactor.floating_info.SetFloatingInfoRequestUseCase
import dev.olog.domain.interactor.tab.ViewPagerLastPageUseCase
import dev.olog.presentation.service_floating_info.FloatingInfoServiceBinder
import dev.olog.presentation.service_floating_info.FloatingInfoServiceHelper
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
        private val setFloatingInfoRequestUseCase: SetFloatingInfoRequestUseCase,
        private val floatingInfoClass: FloatingInfoServiceBinder,
        private val viewPagerLastPageUseCase: ViewPagerLastPageUseCase
) {

    fun startFloatingService(activity: Activity, songTitle: String?){
        songTitle?.let { setFloatingInfoRequestUseCase.execute(it) }

        FloatingInfoServiceHelper.startServiceIfHasOverlayPermission(activity, floatingInfoClass)
    }

    fun getViewPagerLastPage() = viewPagerLastPageUseCase.get()
    fun setViewPagerLastPage(page: Int) = viewPagerLastPageUseCase.set(page)

}