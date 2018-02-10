package dev.olog.msc.domain.interactor.prefs

import dev.olog.msc.domain.entity.LibraryCategoryBehavior
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.PrefsUseCase
import javax.inject.Inject

class CategoriesBehaviorUseCase @Inject constructor(
        private val gateway: AppPreferencesGateway

): PrefsUseCase<List<LibraryCategoryBehavior>>() {

    override fun get(): List<LibraryCategoryBehavior> {
        return gateway.getLibraryCategoriesBehavior()
    }

    fun getDefault(): List<LibraryCategoryBehavior> {
        return gateway.getDefaultLibraryCategoriesBehavior()
    }

    override fun set(param: List<LibraryCategoryBehavior>) {
        gateway.setLibraryCategoriesBehavior(param)
    }
}