package dev.olog.domain.interactor.service

import dev.olog.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.domain.interactor.PrefsUseCase
import javax.inject.Inject

class BookmarkUseCase @Inject constructor(
        private val dataStore: MusicPreferencesGateway
) : PrefsUseCase<Long>() {

    override fun get() = dataStore.getBookmark()

    override fun set(param: Long) {
        dataStore.setBookmark(param)
    }



}
