package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class ViewPagerLastPageUseCase @Inject constructor(
        private val appPreferencesGateway: AppPreferencesGateway

){

    fun get(): Int = appPreferencesGateway.getViewPagerLastVisitedPage()
    fun set(lastPage: Int) = appPreferencesGateway.setViewPagerLastVisitedPage(lastPage)

}