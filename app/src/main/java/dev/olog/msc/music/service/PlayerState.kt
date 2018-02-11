package dev.olog.msc.music.service

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.R
import dev.olog.msc.app.AppShortcuts
import dev.olog.msc.constants.WidgetConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.music.service.model.PositionInQueue
import dev.olog.msc.presentation.app.widget.WidgetClasses
import dev.olog.msc.utils.k.extension.getAppWidgetsIdsFor
import javax.inject.Inject

@PerService
class PlayerState @Inject constructor(
        @ApplicationContext private val context: Context,
        private val mediaSession: MediaSessionCompat,
        private val musicPreferencesUseCase: MusicPreferencesUseCase,
        private val appShortcuts: AppShortcuts,
        private val widgetClasses: WidgetClasses

){

    private val builder = PlaybackStateCompat.Builder()
    private var activeQueueId = MediaSessionCompat.QueueItem.UNKNOWN_ID.toLong()

    init {
        builder.setState(PlaybackStateCompat.STATE_PAUSED, musicPreferencesUseCase.getBookmark(), 0f)
                .setActions(getActions())
    }

    fun prepare(id: Long, bookmark: Long) {
        builder.setActiveQueueItemId(id)
        mediaSession.setPlaybackState(builder.build())

        notifyWidgetsOfStateChanged(false, bookmark)
    }

    fun update(state: Int, bookmark: Long): PlaybackStateCompat {
        return update(state, bookmark, null)
    }

    /**
     * @param state one of: PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_PAUSED
     */
    fun update(state: Int, bookmark: Long, id: Long?): PlaybackStateCompat {
        val isPlaying = state == PlaybackStateCompat.STATE_PLAYING

        if (isPlaying){
            disablePlayShortcut()
        } else {
            enablePlayShortcut()
        }

        builder.setState(state, bookmark, (if (isPlaying) 1 else 0).toFloat())

        musicPreferencesUseCase.setBookmark(bookmark)

        if (id != null) {
            activeQueueId = id
            builder.setActiveQueueItemId(activeQueueId)
        }

        val playbackState = builder.build()
        mediaSession.setPlaybackState(playbackState)

        notifyWidgetsOfStateChanged(isPlaying, bookmark)

        return playbackState
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
            positionInQueue == PositionInQueue.BOTH -> {
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

    fun setEmptyQueue(){
        val localBuilder = PlaybackStateCompat.Builder(builder.build())
        localBuilder.setState(PlaybackStateCompat.STATE_ERROR, 0, 0f)
                .setErrorMessage(PlaybackStateCompat.ERROR_CODE_UNKNOWN_ERROR, context.getString(R.string.music_error_empty_queue))

        mediaSession.setPlaybackState(localBuilder.build())
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

    private fun notifyWidgetsOfStateChanged(isPlaying: Boolean, bookmark: Long){
        for (clazz in widgetClasses.get()) {
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

    private fun notifyWidgetsActionChanged(showPrevious: Boolean, showNext: Boolean){
        for (clazz in widgetClasses.get()) {
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

    private fun disablePlayShortcut(){
        appShortcuts.disablePlay()
    }


    private fun enablePlayShortcut(){
        appShortcuts.enablePlay()
    }

}