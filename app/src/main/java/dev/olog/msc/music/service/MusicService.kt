package dev.olog.msc.music.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import dagger.Lazy
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.domain.interactor.prefs.SleepTimerUseCase
import dev.olog.msc.music.service.helper.CarHelper
import dev.olog.msc.music.service.helper.MediaIdHelper
import dev.olog.msc.music.service.helper.MediaItemGenerator
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.shared_android.PendingIntents
import dev.olog.shared_android.interfaces.MainActivityClass
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
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
    @Inject lateinit var mediaItemGenerator: Lazy<MediaItemGenerator>

    private val subsriptions = CompositeDisposable()

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
        subsriptions.clear()
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
    }

    override fun handlePlayFromVoiceSearch(intent: Intent) {
        val voiceParams = intent.extras
        val query = voiceParams.getString(SearchManager.QUERY)
        callback.onPlayFromSearch(query, voiceParams)
    }

    private fun resetSleepTimer(){
        sleepTimerUseCase.reset()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(PendingIntents.stopServiceIntent(this, this::class.java))
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        if (clientPackageName == packageName){
            return BrowserRoot(MediaIdHelper.MEDIA_ID_ROOT, null)
        }

        if (CarHelper.isValidCarPackage(clientPackageName)){
            return BrowserRoot(MediaIdHelper.MEDIA_ID_ROOT, null)
        }
        return null
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        if (parentId == MediaIdHelper.MEDIA_ID_ROOT){
            grantUriPermission(CarHelper.AUTO_APP_PACKAGE_NAME,
                    Uri.parse("content://media/external/audio/albumart"),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION)
            result.sendResult(MediaIdHelper.getLibraryCategories(this))
            return
        }
        val mediaIdCategory = MediaIdCategory.values()
                .toList()
                .firstOrNull { it.toString() == parentId }

        if (mediaIdCategory != null){
            result.detach()
            mediaItemGenerator.get().getCategoryChilds(mediaIdCategory)
                    .map { it.toMutableList() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result::sendResult, Throwable::printStackTrace)
                    .addTo(subsriptions)
            return
        }
        val mediaId = MediaId.fromString(parentId)
        result.detach()

        mediaItemGenerator.get().getCategoryValueChilds(mediaId)
                .map { it.toMutableList() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result::sendResult, Throwable::printStackTrace)
                .addTo(subsriptions)

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