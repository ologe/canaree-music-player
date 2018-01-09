package dev.olog.domain.interactor

import dev.olog.domain.entity.LibraryCategoryBehavior
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.domain.interactor.base.PrefsUseCase
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