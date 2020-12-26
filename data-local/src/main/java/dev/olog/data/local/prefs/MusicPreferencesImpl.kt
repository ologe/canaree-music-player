package dev.olog.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.LastMetadata
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.data.local.R
import dev.olog.shared.android.extensions.observeKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.seconds

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

private const val LAST_PROGRESSIVE = "$TAG.last_progressive"

private const val MUSIC_VOLUME = "$TAG.music_volume"

class MusicPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences

): MusicPreferencesGateway {

    override var bookmark: Duration
        get() = preferences.getLong(BOOKMARK, 0).milliseconds
        set(value) {
            preferences.edit {
                putLong(BOOKMARK, value.toLongMilliseconds())
            }
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
        setMidnightMode(false)
        crossfade = 0.milliseconds
        isGapless = false
    }

    private fun setMidnightMode(enable: Boolean){
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_midnight_mode_key), enable)
        }
    }

    override var crossfade: Duration
        get() = preferences.getInt(context.getString(R.string.prefs_cross_fade_key), 0).seconds
        set(value) {
            preferences.edit {
                putInt(context.getString(R.string.prefs_cross_fade_key), value.inSeconds.toInt())
            }
        }
    override var isGapless: Boolean
        get() = preferences.getBoolean(context.getString(R.string.prefs_gapless_key), false)
        set(value) {
            preferences.edit {
                putBoolean(context.getString(R.string.prefs_gapless_key), value)
            }
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

    override var lastProgressive: Int
        get() = preferences.getInt(LAST_PROGRESSIVE, 0)
        set(value) {
            preferences.edit { putInt(LAST_PROGRESSIVE, value) }
        }

    override fun observeLastProgressive(): Flow<Int> {
        return preferences.observeKey(LAST_PROGRESSIVE, 0)
    }

    override var volume: Int
        get() = preferences.getInt(MUSIC_VOLUME, 100).coerceIn(0, 100)
        set(value) {
            preferences.edit {
                putInt(MUSIC_VOLUME, value.coerceIn(0, 100))
            }
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