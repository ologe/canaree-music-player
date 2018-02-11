package dev.olog.msc.domain.interactor.prefs

import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPreferencesUseCase @Inject constructor(
        private val gateway: MusicPreferencesGateway
) {

    fun getLastIdInPlaylist(): Int = gateway.getLastIdInPlaylist()
    fun observeLastIdInPlaylist(): Observable<Int> = gateway.observeLastIdInPlaylist()

    fun setLastIdInPlaylist(idInPlaylist: Int) {
        gateway.setLastIdInPlaylist(idInPlaylist)
    }

    fun getRepeatMode(): Int = gateway.getRepeatMode()
    fun setRepeatMode(mode: Int) {
        gateway.setRepeatMode(mode)
    }

    fun getShuffleMode(): Int = gateway.getShuffleMode()
    fun setShuffleMode(mode: Int) {
        gateway.setShuffleMode(mode)
    }

    fun getBookmark() : Long = gateway.getBookmark()
    fun setBookmark(bookmark: Long) {
        gateway.setBookmark(bookmark)
    }

    fun isMidnightMode() : Observable<Boolean> = gateway.isMidnightMode()
    fun setMidnightMode(enabled: Boolean) {
        gateway.setMidnightMode(enabled)
    }

    fun setSkipToPreviousVisibility(visible: Boolean) {
        gateway.setSkipToPreviousVisibility(visible)
    }
    fun observeSkipToPreviousVisibility(): Observable<Boolean> = gateway.observeSkipToPreviousVisibility()

    fun setSkipToNextVisibility(visible: Boolean) {
        gateway.setSkipToNextVisibility(visible)
    }
    fun observeSkipToNextVisibility(): Observable<Boolean> = gateway.observeSkipToNextVisibility()

    fun observeLastMetadata(): Observable<String> {
        return gateway.observeLastMetadata()
    }

    fun getLastTitle(): String = gateway.getLastTitle()
    fun setLastTitle(title: String) {
        gateway.setLastTitle(title)
    }

    fun getLastSubtitle(): String = gateway.getLastSubtitle()
    fun setLastSubtitle(subtitle: String){
        gateway.setLastSubtitle(subtitle)
    }

}