package dev.olog.feature.media.state

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.feature.media.api.MusicPreferencesGateway
import dev.olog.feature.widget.api.WidgetConstants
import dev.olog.feature.media.api.model.PositionInQueue
import dev.olog.feature.media.api.model.SkipType
import dev.olog.feature.widget.api.FeatureWidgetNavigator
import dev.olog.shared.extension.getAppWidgetsIdsFor
import javax.inject.Inject

@ServiceScoped
internal class MusicServicePlaybackState @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaSession: MediaSessionCompat,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val featureWidgetNavigator: FeatureWidgetNavigator,
) {

    companion object {
        @JvmStatic
        val TAG = "SM:${MusicServicePlaybackState::class.java.simpleName}"
    }

    private val builder = PlaybackStateCompat.Builder().apply {
        setState(
            PlaybackStateCompat.STATE_PAUSED,
            musicPreferencesUseCase.getBookmark(),
            0f
        )
        setActions(getActions())
    }

    fun prepare(bookmark: Long) {
        Log.v(TAG, "prepare bookmark=$bookmark")
        mediaSession.setPlaybackState(builder.build())

        notifyWidgetsOfStateChanged(false, bookmark)
    }

    /**
     * @param state one of: PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_PAUSED
     */
    fun update(state: Int, bookmark: Long, speed: Float): PlaybackStateCompat {
        Log.v(TAG, "update state=$state, bookmark=$bookmark, speed=$speed")

        val isPlaying = state == PlaybackStateCompat.STATE_PLAYING

        builder.setState(state, bookmark, (if (isPlaying) speed else 0f))

        musicPreferencesUseCase.setBookmark(bookmark)

        val playbackState = builder.build()

        notifyWidgetsOfStateChanged(isPlaying, bookmark)

        mediaSession.setPlaybackState(playbackState)

        return playbackState
    }

    fun updatePlaybackSpeed(speed: Float) {
        Log.v(TAG, "updatePlaybackSpeed speed=$speed")
        val currentState = mediaSession.controller?.playbackState
        if (currentState == null) {
            builder.setState(
                PlaybackStateCompat.STATE_PAUSED,
                musicPreferencesUseCase.getBookmark(),
                0f
            )
        } else {
            val stateSpeed =
                if (currentState.state == PlaybackStateCompat.STATE_PLAYING) speed else 0f
            builder.setState(currentState.state, currentState.position, stateSpeed)
        }
        mediaSession.setPlaybackState(builder.build())
    }

    fun toggleSkipToActions(positionInQueue: PositionInQueue) {
        Log.v(TAG, "toggleSkipToActions positionInQueue=$positionInQueue")
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

    fun skipTo(skipType: SkipType) {
        Log.v(TAG, "skipTo skipType=$skipType")

        val state = when (skipType){
            SkipType.SKIP_NEXT -> PlaybackStateCompat.STATE_SKIPPING_TO_NEXT
            SkipType.SKIP_PREVIOUS -> PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS
            SkipType.NONE,
            SkipType.RESTART,
            SkipType.TRACK_ENDED -> error("skip type=$skipType")
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
        Log.v(TAG, "notify widgets state changed isPlaying=$isPlaying, bookmark=$bookmark")
        for (clazz in featureWidgetNavigator.widgetClasses()) {
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
        Log.v(TAG, "notify widgets actions changed showPrevious=$showPrevious, showNext=$showNext")
        for (clazz in featureWidgetNavigator.widgetClasses()) {
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

}