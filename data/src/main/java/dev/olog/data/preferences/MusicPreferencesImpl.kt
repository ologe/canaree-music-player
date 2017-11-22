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
        private const val SONG_ID = TAG + ".SONG_ID"
        private const val SHUFFLE_MODE = TAG + ".SHUFFLE_MODE"
        private const val REPEAT_MODE = TAG + ".REPEAT_MODE"
    }

    override fun getBookmark(): Long {
        return preferences.getLong(BOOKMARK, 0)
    }

    override fun setBookmark(bookmark: Long) {
        preferences.edit { putLong(BOOKMARK, bookmark) }
    }

    override fun getCurrentSongId(): Long {
        return preferences.getLong(SONG_ID, 0)
    }

    override fun setCurrentSongId(songId: Long) {
        preferences.edit { putLong(SONG_ID, songId) }
    }

    override fun observeCurrentSongId(): Flowable<Long> {
        return rxPreferences.getLong(SONG_ID)
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

}