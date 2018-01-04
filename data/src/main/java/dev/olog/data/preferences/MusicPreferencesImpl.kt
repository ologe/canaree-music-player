package dev.olog.data.preferences

import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.data.utils.edit
import dev.olog.domain.gateway.prefs.MusicPreferencesGateway
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

class MusicPreferencesImpl @Inject constructor(
        private val preferences: SharedPreferences,
        private val rxPreferences: RxSharedPreferences

): MusicPreferencesGateway {

    companion object {
        private const val TAG = "MusicPreferencesDataStoreImpl"
        private const val BOOKMARK = TAG + ".BOOKMARK"
        private const val ID_IN_PLAYLIST = TAG + ".ID_IN_PLAYLIST"
        private const val SHUFFLE_MODE = TAG + ".SHUFFLE_MODE"
        private const val REPEAT_MODE = TAG + ".REPEAT_MODE"

        private const val SKIP_PREVIOUS = TAG + ".SKIP_PREVIOUS"
        private const val SKIP_NEXT = TAG + ".SKIP_NEXT"
    }

    override fun getBookmark(): Long {
        return preferences.getLong(BOOKMARK, 0)
    }

    override fun setBookmark(bookmark: Long) {
        preferences.edit { putLong(BOOKMARK, bookmark) }
    }

    override fun getCurrentIdInPlaylist(): Int {
        return preferences.getInt(ID_IN_PLAYLIST, 0)
    }

    override fun setCurrentIdInPlaylist(idInPlaylist: Int) {
        preferences.edit { putInt(ID_IN_PLAYLIST, idInPlaylist) }
    }

    override fun observeCurrentIdInPlaylist(): Flowable<Int> {
        return rxPreferences.getInteger(ID_IN_PLAYLIST)
                .asObservable().toFlowable(BackpressureStrategy.LATEST)
    }

    override fun getRepeatMode(): Int {
        return preferences.getInt(REPEAT_MODE, 0)
    }

    override fun setRepeatMode(repeatMode: Int) {
        preferences.edit { putInt(REPEAT_MODE, repeatMode) }
    }

    override fun getShuffleMode(): Int {
        return preferences.getInt(SHUFFLE_MODE, 0)
    }

    override fun setShuffleMode(shuffleMode: Int) {
        preferences.edit { putInt(SHUFFLE_MODE, shuffleMode) }
    }

    override fun setSkipToPreviousVisibility(visible: Boolean) {
        preferences.edit { putBoolean(SKIP_PREVIOUS, visible) }
    }

    override fun observeSkipToPreviousVisibility(): Flowable<Boolean> {
        return rxPreferences.getBoolean(SKIP_PREVIOUS, true).asObservable()
                .toFlowable(BackpressureStrategy.LATEST)
    }

    override fun setSkipToNextVisibility(visible: Boolean) {
        preferences.edit { putBoolean(SKIP_NEXT, visible) }
    }

    override fun observeSkipToNextVisibility(): Flowable<Boolean> {
        return rxPreferences.getBoolean(SKIP_NEXT, true).asObservable()
                .toFlowable(BackpressureStrategy.LATEST)
    }
}