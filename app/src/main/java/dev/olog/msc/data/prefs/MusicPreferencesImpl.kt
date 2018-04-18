package dev.olog.msc.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.LastMetadata
import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

private const val TAG = "MusicPreferences"

private const val BOOKMARK = "$TAG.bookmark"
private const val SHUFFLE_MODE = "$TAG.mode.shuffle"
private const val REPEAT_MODE = "$TAG.mode.repeat"

private const val ID_IN_PLAYLIST = "$TAG.id.in.playlist"

private const val SKIP_PREVIOUS = "$TAG.skip.previous"
private const val SKIP_NEXT = "$TAG.skip.next"

private const val LAST_TITLE = "$TAG.last.title"
private const val LAST_SUBTITLE = "$TAG.last.subtitle"
private const val LAST_IMAGE = "$TAG.last.image"
private const val LAST_ID = "$TAG.last.id"

class MusicPreferencesImpl @Inject constructor(
        @ApplicationContext private val context: Context,
        private val preferences: SharedPreferences,
        private val rxPreferences: RxSharedPreferences

): MusicPreferencesGateway {

    override fun getBookmark(): Long {
        return preferences.getLong(BOOKMARK, 0)
    }

    override fun setBookmark(bookmark: Long) {
        preferences.edit { putLong(BOOKMARK, bookmark) }
    }

    override fun getLastIdInPlaylist(): Int {
        return preferences.getInt(ID_IN_PLAYLIST, 0)
    }

    override fun setLastIdInPlaylist(idInPlaylist: Int) {
        preferences.edit { putInt(ID_IN_PLAYLIST, idInPlaylist) }
    }

    override fun observeLastIdInPlaylist(): Observable<Int> {
        return rxPreferences.getInteger(ID_IN_PLAYLIST).asObservable()
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

    override fun observeSkipToPreviousVisibility(): Observable<Boolean> {
        return rxPreferences.getBoolean(SKIP_PREVIOUS, true).asObservable()
    }

    override fun setSkipToNextVisibility(visible: Boolean) {
        preferences.edit { putBoolean(SKIP_NEXT, visible) }
    }

    override fun observeSkipToNextVisibility(): Observable<Boolean> {
        return rxPreferences.getBoolean(SKIP_NEXT, true).asObservable()
    }

    override fun isMidnightMode(): Observable<Boolean> {
        val key = context.getString(R.string.prefs_midnight_mode_key)
        return rxPreferences.getBoolean(key, false)
                .asObservable()
    }

    override fun getLastMetadata(): LastMetadata {
        return LastMetadata(
                preferences.getString(LAST_TITLE, ""),
                preferences.getString(LAST_SUBTITLE, ""),
                preferences.getString(LAST_IMAGE, ""),
                preferences.getLong(LAST_ID, -1)
        )
    }

    override fun setLastMetadata(metadata: LastMetadata) {
        val (title, subtitle, image) = metadata
        preferences.edit {
            putString(LAST_TITLE, title)
            putString(LAST_SUBTITLE, subtitle)
            putString(LAST_IMAGE, image)
        }
    }

    override fun observeLastMetadata(): Observable<LastMetadata> {
        return rxPreferences.getString(LAST_TITLE)
                .asObservable()
                .map { getLastMetadata() }
    }

    override fun setDefault(): Completable {
        return Completable.create { emitter ->
            setMidnightMode(false)

            emitter.onComplete()
        }
    }

    private fun setMidnightMode(enable: Boolean){
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_midnight_mode_key), enable)
        }
    }

    override fun observeCrossFade(): Observable<Int> {
        val key = context.getString(R.string.prefs_cross_fade_key)
        return rxPreferences.getInteger(key, 0)
                .asObservable()
    }

    override fun observeGapless(): Observable<Boolean> {
        val key = context.getString(R.string.prefs_gapless_key)
        return rxPreferences.getBoolean(key, false)
                .asObservable()
    }
}