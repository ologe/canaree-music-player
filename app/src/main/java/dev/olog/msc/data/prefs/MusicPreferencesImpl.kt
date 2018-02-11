package dev.olog.msc.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.content.edit
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import io.reactivex.Observable
import javax.inject.Inject

private const val TAG = "MusicPreferencesImpl"

private const val BOOKMARK = TAG + ".bookmark"
private const val SHUFFLE_MODE = TAG + ".mode.shuffle"
private const val REPEAT_MODE = TAG + ".mode.repeat"

private const val ID_IN_PLAYLIST = TAG + ".id.in.playlist"

private const val SKIP_PREVIOUS = TAG + ".skip.previous"
private const val SKIP_NEXT = TAG + ".skip.next"

private const val LAST_TITLE = TAG + ".last.title"
private const val LAST_SUBTITLE = TAG + ".last.subtitle"

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

    override fun setMidnightMode(enabled: Boolean) {
        val key = context.getString(R.string.prefs_midnight_mode_key)
        preferences.edit { putBoolean(key, enabled) }
    }

    override fun getLastTitle(): String {
        return preferences.getString(LAST_TITLE, "")
    }

    override fun setLastTitle(title: String) {
        preferences.edit { putString(LAST_TITLE, title) }
    }

    override fun getLastSubtitle(): String {
        return preferences.getString(LAST_SUBTITLE, "")
    }

    override fun setLastSubtitle(subtitle: String) {
        preferences.edit { putString(LAST_SUBTITLE, subtitle) }
    }

    override fun observeLastMetadata(): Observable<String> {
        return rxPreferences.getString(LAST_TITLE + "|" + LAST_SUBTITLE)
                .asObservable()
    }

}