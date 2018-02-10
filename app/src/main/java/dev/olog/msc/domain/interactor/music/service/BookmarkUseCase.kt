package dev.olog.msc.domain.interactor.music.service

import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.domain.interactor.base.PrefsUseCase
import javax.inject.Inject

class BookmarkUseCase @Inject constructor(
        private val dataStore: MusicPreferencesGateway
) : PrefsUseCase<Long>() {

    override fun get() = dataStore.getBookmark()

    override fun set(param: Long) {
        dataStore.setBookmark(param)
    }



}
