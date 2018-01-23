package dev.olog.music_service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import dev.olog.domain.interactor.prefs.SleepTimerUseCase
import dev.olog.shared.MediaId
import dev.olog.shared.constants.MusicConstants
import dev.olog.shared_android.PendingIntents
import dev.olog.shared_android.interfaces.MainActivityClass
import javax.inject.Inject

class MusicService : BaseMusicService() {

    companion object {
        const val TAG = "MusicService"
    }

    @Inject lateinit var activityClass : MainActivityClass
    @Inject lateinit var mediaSession: MediaSessionCompat
    @Inject lateinit var callback: MediaSessionCallback

    @Inject lateinit var currentSong : CurrentSong
    @Inject lateinit var playerMetadata: PlayerMetadata
    @Inject lateinit var notification: MusicNotificationManager
    @Inject lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun onCreate() {
        super.onCreate()
        sessionToken = mediaSession.sessionToken

        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or
                MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)

        mediaSession.setMediaButtonReceiver(buildMediaButtonReceiverPendingIntent())
        mediaSession.setSessionActivity(buildSessionActivityPendingIntent())
        mediaSession.setRatingType(RatingCompat.RATING_HEART)
        mediaSession.setCallback(callback)

        mediaSession.isActive = true
    }

    override fun onDestroy() {
        super.onDestroy()
        resetSleepTimer()
        mediaSession.setMediaButtonReceiver(null)
        mediaSession.setCallback(null)
        mediaSession.isActive = false
        mediaSession.release()
    }

    override fun handleAppShortcutPlay(intent: Intent) {
        mediaSession.controller.transportControls.play()
    }

    override fun handleAppShortcutShuffle(intent: Intent) {
        val bundle = Bundle()
        bundle.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, MediaId.shuffleAllId().toString())
        mediaSession.controller.transportControls.sendCustomAction(
                MusicConstants.ACTION_PLAY_SHUFFLE, bundle)
    }

    override fun handlePlayPause(intent: Intent) {
        callback.handlePlayPause()
    }

    override fun handleSkipNext(intent: Intent) {
        callback.onSkipToNext()
    }

    override fun handleSkipPrevious(intent: Intent) {
        callback.onSkipToPrevious()
    }

    override fun handleMediaButton(intent: Intent) {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
    }

    override fun handleSleepTimerEnd(intent: Intent) {
        sleepTimerUseCase.reset()
        mediaSession.controller.transportControls.pause()
        stop()
    }

    private fun resetSleepTimer(){
        sleepTimerUseCase.reset()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(PendingIntents.stopServiceIntent(this, this::class.java))
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {

    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(MediaId.MEDIA_ID_ROOT, null)
    }

    private fun buildMediaButtonReceiverPendingIntent(): PendingIntent {
        val intent = Intent(Intent.ACTION_MEDIA_BUTTON)
        intent.setClass(this, this.javaClass)

        return PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildSessionActivityPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(this, 0,
                Intent(this, activityClass.get()), PendingIntent.FLAG_CANCEL_CURRENT)
    }
}