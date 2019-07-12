package dev.olog.service.music.state

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.injection.dagger.PerService
import dev.olog.service.music.R
import dev.olog.service.music.model.PositionInQueue
import dev.olog.shared.Classes
import dev.olog.shared.WidgetConstants
import dev.olog.shared.extensions.getAppWidgetsIdsFor
import javax.inject.Inject

@PerService
class MusicServicePlaybackState @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaSession: MediaSessionCompat,
    private val musicPreferencesUseCase: MusicPreferencesGateway

) {

    companion object {
        const val CUSTOM_ACTION_ADD_FAVORITE = "PlayerState.add.favorite"
        const val CUSTOM_ACTION_SHUFFLE = "PlayerState.shuffle"
        const val CUSTOM_ACTION_REPEAT = "PlayerState.repeat"
    }

    private val appShortcuts = AppShortcuts.instance(context)

    private val builder = PlaybackStateCompat.Builder()

    init {
        builder.setState(
            PlaybackStateCompat.STATE_PAUSED,
            musicPreferencesUseCase.getBookmark(),
            0f
        ).setActions(getActions())
            .addCustomAction(addToFavoriteAction())
            .addCustomAction(repeatAction())
            .addCustomAction(shuffleAction()) // TODO
    }

    private fun addToFavoriteAction(): PlaybackStateCompat.CustomAction {
        val action =
            CUSTOM_ACTION_ADD_FAVORITE
        val name = "Add favorite"
        return PlaybackStateCompat.CustomAction.Builder(action, name,
            R.drawable.vd_favorite
        )
            .build()
    }

    private fun repeatAction(): PlaybackStateCompat.CustomAction {
        val action =
            CUSTOM_ACTION_REPEAT
        val name = "Repeat"
        return PlaybackStateCompat.CustomAction.Builder(action, name,
            R.drawable.vd_repeat
        )
            .build()
    }

    private fun shuffleAction(): PlaybackStateCompat.CustomAction {
        val action =
            CUSTOM_ACTION_SHUFFLE
        val name = "Shuffle"
        return PlaybackStateCompat.CustomAction.Builder(action, name,
            R.drawable.vd_shuffle
        )
            .build()
    }

    fun prepare(id: Long, bookmark: Long) {
        builder.setActiveQueueItemId(id)
        mediaSession.setPlaybackState(builder.build())

        notifyWidgetsOfStateChanged(false, bookmark)
    }

    /**
     * @param state one of: PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_PAUSED
     */
    fun update(state: Int, bookmark: Long, speed: Float): PlaybackStateCompat {
        val isPlaying = state == PlaybackStateCompat.STATE_PLAYING

        if (isPlaying) {
            disablePlayShortcut()
        } else {
            enablePlayShortcut()
        }

        builder.setState(state, bookmark, (if (isPlaying) speed else 0f))

        musicPreferencesUseCase.setBookmark(bookmark)

        val playbackState = builder.build()

        notifyWidgetsOfStateChanged(isPlaying, bookmark)

        try {
            mediaSession.setPlaybackState(playbackState)
        } catch (ignored: IllegalStateException) {
            // random crash
        }

        return playbackState
    }

    fun updatePlaybackSpeed(speed: Float) {
        val currentState = mediaSession.controller?.playbackState
        if (currentState == null) {
            builder.setState(
                PlaybackStateCompat.STATE_PAUSED,
                musicPreferencesUseCase.getBookmark(),
                0f
            )
        } else {
            val stateSpeed = if (currentState.state == PlaybackStateCompat.STATE_PLAYING) speed else 0f
            builder.setState(currentState.state, currentState.position, stateSpeed)
        }
        mediaSession.setPlaybackState(builder.build())
    }

    fun toggleSkipToActions(positionInQueue: PositionInQueue) {

        when {
            positionInQueue === PositionInQueue.FIRST -> {
                musicPreferencesUseCase.setSkipToPreviousVisibility(false)
                musicPreferencesUseCase.setSkipToNextVisibility(true)
                notifyWidgetsActionChanged(false, true)
            }
            positionInQueue === PositionInQueue.LAST -> {
                musicPreferencesUseCase.setSkipToPreviousVisibility(true)
                musicPreferencesUseCase.setSkipToNextVisibility(false)
                notifyWidgetsActionChanged(true, false)
            }
            positionInQueue === PositionInQueue.IN_MIDDLE -> {
                musicPreferencesUseCase.setSkipToPreviousVisibility(true)
                musicPreferencesUseCase.setSkipToNextVisibility(true)
                notifyWidgetsActionChanged(true, true)
            }
            positionInQueue == PositionInQueue.FIRST_AND_LAST -> {
                musicPreferencesUseCase.setSkipToPreviousVisibility(false)
                musicPreferencesUseCase.setSkipToNextVisibility(false)
                notifyWidgetsActionChanged(false, false)
            }
        }

    }

    fun skipTo(toNext: Boolean) {
        val state = if (toNext) {
            PlaybackStateCompat.STATE_SKIPPING_TO_NEXT
        } else {
            PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS
        }
        builder.setState(state, 0, 1f)

        mediaSession.setPlaybackState(builder.build())
    }

    private fun getActions(): Long {
        return PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_STOP or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
                PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM or
                PlaybackStateCompat.ACTION_SEEK_TO or
                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or
                PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
//                PlaybackStateCompat.ACTION_SET_RATING or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
    }

    private fun notifyWidgetsOfStateChanged(isPlaying: Boolean, bookmark: Long) {
        for (clazz in Classes.widgets) {
            val ids = context.getAppWidgetsIdsFor(clazz)

            val intent = Intent(context, clazz).apply {
                action = WidgetConstants.STATE_CHANGED
                putExtra(WidgetConstants.ARGUMENT_IS_PLAYING, isPlaying)
                putExtra(WidgetConstants.ARGUMENT_BOOKMARK, bookmark)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }

            context.sendBroadcast(intent)
        }
    }

    private fun notifyWidgetsActionChanged(showPrevious: Boolean, showNext: Boolean) {
        for (clazz in Classes.widgets) {
            val ids = context.getAppWidgetsIdsFor(clazz)

            val intent = Intent(context, clazz).apply {
                action = WidgetConstants.ACTION_CHANGED
                putExtra(WidgetConstants.ARGUMENT_SHOW_PREVIOUS, showPrevious)
                putExtra(WidgetConstants.ARGUMENT_SHOW_NEXT, showNext)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }

            context.sendBroadcast(intent)
        }
    }

    private fun disablePlayShortcut() {
        appShortcuts.disablePlay()
    }


    private fun enablePlayShortcut() {
        appShortcuts.enablePlay()
    }

}