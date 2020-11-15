package dev.olog.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.LastMetadata
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.data.R
import dev.olog.data.utils.assertBackgroundThread
import dev.olog.shared.android.extensions.observeKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "MusicPreferences"

private const val BOOKMARK = "$TAG.bookmark"
private const val SHUFFLE_MODE = "$TAG.mode.shuffle"
private const val REPEAT_MODE = "$TAG.mode.repeat"

private const val SKIP_PREVIOUS = "$TAG.skip.previous"
private const val SKIP_NEXT = "$TAG.skip.next"

private const val LAST_TITLE = "$TAG.last.title"
private const val LAST_SUBTITLE = "$TAG.last.subtitle"
private const val LAST_ID = "$TAG.last.id"

private const val PLAYBACK_SPEED = "$TAG.playback_speed"

private const val LAST_ID_IN_PLAYLIST = "$TAG.last_id_in_playlist_2"

private const val MUSIC_VOLUME = "$TAG.music_volume"

class MusicPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences

): MusicPreferencesGateway {

    override fun getBookmark(): Long {
        return preferences.getLong(BOOKMARK, 0)
    }

    override fun setBookmark(bookmark: Long) {
        preferences.edit { putLong(BOOKMARK, bookmark) }
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

    override fun observeSkipToPreviousVisibility(): Flow<Boolean> {
        return preferences.observeKey(SKIP_PREVIOUS, true)
    }

    override fun setSkipToNextVisibility(visible: Boolean) {
        preferences.edit { putBoolean(SKIP_NEXT, visible) }
    }

    override fun observeSkipToNextVisibility(): Flow<Boolean> {
        return preferences.observeKey(SKIP_NEXT, true)
    }

    override fun isMidnightMode(): Flow<Boolean> {
        val key = context.getString(R.string.prefs_midnight_mode_key)
        return preferences.observeKey(key, false)
    }

    override fun getLastMetadata(): LastMetadata {
        return LastMetadata(
            preferences.getString(LAST_TITLE, "")!!,
            preferences.getString(LAST_SUBTITLE, "")!!,
            preferences.getLong(LAST_ID, -1)
        )
    }

    override fun setLastMetadata(metadata: LastMetadata) {
        preferences.edit {
            putString(LAST_TITLE, metadata.title)
            putString(LAST_SUBTITLE, metadata.subtitle)
        }
    }

    override fun observeLastMetadata(): Flow<LastMetadata> {
        return preferences.observeKey(LAST_TITLE, "")
                .map { getLastMetadata() }
    }

    override fun setDefault() {
        assertBackgroundThread()
        setMidnightMode(false)
        setCrossFade(0)
        setGapless(false)
    }

    private fun setMidnightMode(enable: Boolean){
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_midnight_mode_key), enable)
        }
    }

    private fun setCrossFade(value: Int){
        val key = context.getString(R.string.prefs_cross_fade_key)
        preferences.edit { putInt(key, value) }
    }

    override fun observeCrossFade(): Flow<Int> {
        return preferences.observeKey(context.getString(R.string.prefs_cross_fade_key), 0)
                .map { it * 1000 }
    }

    override fun observeGapless(): Flow<Boolean> {
        return preferences.observeKey(context.getString(R.string.prefs_gapless_key), false)
    }

    private fun setGapless(enabled: Boolean){
        val key = context.getString(R.string.prefs_gapless_key)
        preferences.edit { putBoolean(key, enabled) }
    }

    override fun observePlaybackSpeed(): Flow<Float> {
        return preferences.observeKey(PLAYBACK_SPEED, 1f)
    }

    override fun setPlaybackSpeed(speed: Float) {
        preferences.edit {
            putFloat(PLAYBACK_SPEED, speed)
        }
    }

    override fun getPlaybackSpeed(): Float {
        return preferences.getFloat(PLAYBACK_SPEED, 1f)
    }

    override fun setLastIdInPlaylist(position: Int) {
        preferences.edit {
            putInt(LAST_ID_IN_PLAYLIST, position)
        }
    }

    override fun observeLastIdInPlaylist(): Flow<Int> {
        return preferences.observeKey(LAST_ID_IN_PLAYLIST, -1)
            .flowOn(Dispatchers.IO)
    }

    override fun getLastIdInPlaylist(): Int {
        return preferences.getInt(LAST_ID_IN_PLAYLIST, -1)
    }

    override fun setVolume(volume: Int) {
        preferences.edit {
            putInt(MUSIC_VOLUME, volume)
        }
    }

    override fun getVolume(): Int {
        return preferences.getInt(MUSIC_VOLUME, 100)
    }

    override fun observeVolume(): Flow<Int> {
        return preferences.observeKey(MUSIC_VOLUME, 100)
            .flowOn(Dispatchers.IO)
    }

    override fun observeShowLockscreenArtwork(): Flow<Boolean> {
        return preferences.observeKey(context.getString(R.string.prefs_lockscreen_artwork_key), false)
            .flowOn(Dispatchers.IO)
    }
}