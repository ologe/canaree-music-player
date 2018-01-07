package dev.olog.presentation.activity_main

import android.app.Activity
import dev.olog.domain.interactor.IsRepositoryEmptyUseCase
import dev.olog.domain.interactor.floating_info.SetFloatingInfoRequestUseCase
import dev.olog.domain.interactor.tab.ViewPagerLastPageUseCase
import dev.olog.presentation.service_floating_info.FloatingInfoServiceHelper
import dev.olog.shared_android.interfaces.FloatingInfoServiceClass
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
        private val setFloatingInfoRequestUseCase: SetFloatingInfoRequestUseCase,
        private val floatingInfoClass: FloatingInfoServiceClass,
        private val viewPagerLastPageUseCase: ViewPagerLastPageUseCase,
        val isRepositoryEmptyUseCase: IsRepositoryEmptyUseCase
) {

    fun startFloatingService(activity: Activity, songTitle: String?){
        songTitle?.let { setFloatingInfoRequestUseCase.execute(it) }

        FloatingInfoServiceHelper.startServiceIfHasOverlayPermission(activity, floatingInfoClass)
    }

    fun getViewPagerLastPage() = viewPagerLastPageUseCase.get()
    fun setViewPagerLastPage(page: Int) = viewPagerLastPageUseCase.set(page)

}