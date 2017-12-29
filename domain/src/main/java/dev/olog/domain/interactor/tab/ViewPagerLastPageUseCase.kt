package dev.olog.domain.interactor.tab

import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class ViewPagerLastPageUseCase @Inject constructor(
        private val appPreferencesGateway: AppPreferencesGateway

){

    fun get(): Int = appPreferencesGateway.getViewPagerLastVisitedPage()
    fun set(lastPage: Int) = appPreferencesGateway.setViewPagerLastVisitedPage(lastPage)

}