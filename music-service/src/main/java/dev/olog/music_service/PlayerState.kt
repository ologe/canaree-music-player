package dev.olog.music_service

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.domain.interactor.music_service.BookmarkUseCase
import dev.olog.domain.interactor.music_service.ToggleSkipToNextVisibilityUseCase
import dev.olog.domain.interactor.music_service.ToggleSkipToPreviousVisibilityUseCase
import dev.olog.music_service.di.PerService
import dev.olog.music_service.model.PositionInQueue
import javax.inject.Inject

@PerService
class PlayerState @Inject constructor(
        private val mediaSession: MediaSessionCompat,
        private val bookmarkUseCase: BookmarkUseCase,
        private val toggleSkipToNextVisibilityUseCase: ToggleSkipToNextVisibilityUseCase,
        private val toggleSkipToPreviousVisibilityUseCase: ToggleSkipToPreviousVisibilityUseCase

){

    private val builder = PlaybackStateCompat.Builder()
    private var activeQueueId = MediaSessionCompat.QueueItem.UNKNOWN_ID.toLong()

    init {
        builder.setState(PlaybackStateCompat.STATE_PAUSED, bookmarkUseCase.get(), 0f)
                .setActions(getActions())
//        mediaSession.setPlaybackState(builder.build())
    }

    fun prepare(id: Long) {
        builder.setActiveQueueItemId(id)
        mediaSession.setPlaybackState(builder.build())
    }

    fun update(state: Int, bookmark: Long): PlaybackStateCompat {
        return update(state, bookmark, null)
    }

    /**
     * @param state one of: PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_PAUSED
     */
    fun update(state: Int, bookmark: Long, id: Long?): PlaybackStateCompat {
        val isPlaying = state == PlaybackStateCompat.STATE_PLAYING

        builder.setState(state, bookmark, (if (isPlaying) 1 else 0).toFloat())

        bookmarkUseCase.set(bookmark)

        if (id != null) {
            activeQueueId = id
            builder.setActiveQueueItemId(activeQueueId)
        }

        val playbackState = builder.build()
        mediaSession.setPlaybackState(playbackState)
        return playbackState
    }

    fun toggleSkipToActions(positionInQueue: PositionInQueue) {

        when {
            positionInQueue === PositionInQueue.FIRST -> {
                toggleSkipToNextVisibilityUseCase.set(true)
                toggleSkipToPreviousVisibilityUseCase.set(false)
            }
            positionInQueue === PositionInQueue.LAST -> {
                toggleSkipToNextVisibilityUseCase.set(false)
                toggleSkipToPreviousVisibilityUseCase.set(true)
            }
            positionInQueue === PositionInQueue.IN_MIDDLE -> {
                toggleSkipToNextVisibilityUseCase.set(true)
                toggleSkipToPreviousVisibilityUseCase.set(true)
            }
            positionInQueue == PositionInQueue.BOTH -> {
                toggleSkipToNextVisibilityUseCase.set(false)
                toggleSkipToPreviousVisibilityUseCase.set(false)
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
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
                PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM or
                PlaybackStateCompat.ACTION_SEEK_TO or
                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or
                PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
                PlaybackStateCompat.ACTION_SET_RATING or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
    }

}