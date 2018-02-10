package dev.olog.msc.presentation.main

import android.app.Activity
import android.support.v4.math.MathUtils
import dev.olog.msc.domain.interactor.IsRepositoryEmptyUseCase
import dev.olog.msc.domain.interactor.floating.window.SetFloatingInfoRequestUseCase
import dev.olog.msc.domain.interactor.tab.ViewPagerLastPageUseCase
import dev.olog.msc.presentation.FloatingInfoServiceHelper
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

    fun getViewPagerLastPage(totalPages: Int) = MathUtils.clamp(
            viewPagerLastPageUseCase.get(), 0, totalPages)

    fun setViewPagerLastPage(page: Int) = viewPagerLastPageUseCase.set(page)

}