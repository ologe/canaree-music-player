package dev.olog.core.interactor

import dev.olog.core.Resettable
import javax.inject.Inject

class ResetPreferencesUseCase @Inject constructor(
    private val resettables: Set<@JvmSuppressWildcards Resettable>,
) {

    fun execute() {
        resettables.forEach { it.reset() }
    }

}