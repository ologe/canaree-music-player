package dev.olog.service.music

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import dagger.Lazy
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.interactor.SleepTimerUseCase
import dev.olog.service.music.di.inject
import dev.olog.service.music.helper.CarHelper
import dev.olog.service.music.helper.CarHelper.CONTENT_STYLE_BROWSABLE_HINT
import dev.olog.service.music.helper.CarHelper.CONTENT_STYLE_GRID_ITEM_HINT_VALUE
import dev.olog.service.music.helper.CarHelper.CONTENT_STYLE_LIST_ITEM_HINT_VALUE
import dev.olog.service.music.helper.CarHelper.CONTENT_STYLE_PLAYABLE_HINT
import dev.olog.service.music.helper.CarHelper.CONTENT_STYLE_SUPPORTED
import dev.olog.service.music.helper.MediaIdHelper
import dev.olog.service.music.helper.MediaItemGenerator
import dev.olog.service.music.helper.WearHelper
import dev.olog.service.music.notification.MusicNotificationManager
import dev.olog.service.music.scrobbling.LastFmScrobbling
import dev.olog.shared.Classes
import dev.olog.shared.MusicServiceAction
import dev.olog.shared.PendingIntents
import dev.olog.shared.extensions.asServicePendingIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MusicService : BaseMusicService(), CoroutineScope by MainScope() {

    companion object {
        const val TAG = "MusicService"
    }

    @Inject
    internal lateinit var mediaSession: MediaSessionCompat
    @Inject
    internal lateinit var callback: MediaSessionCallback

    @Inject
    internal lateinit var currentSong: CurrentSong
    @Inject
    internal lateinit var playerMetadata: MusicServiceMetadata
    @Inject
    internal lateinit var notification: MusicNotificationManager
    @Inject
    internal lateinit var sleepTimerUseCase: SleepTimerUseCase
    @Inject
    internal lateinit var mediaItemGenerator: Lazy<MediaItemGenerator>
    @Inject
    internal lateinit var alarmManager: AlarmManager
    @Inject
    internal lateinit var lastFmScrobbling: LastFmScrobbling

    override fun onCreate() {
        inject()
        super.onCreate()

        sessionToken = mediaSession.sessionToken

        mediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or
                    MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
        )

        mediaSession.setMediaButtonReceiver(buildMediaButtonReceiverPendingIntent())
        mediaSession.setSessionActivity(buildSessionActivityPendingIntent())
        mediaSession.setRatingType(RatingCompat.RATING_HEART)
        mediaSession.setCallback(callback)
        callback.onPrepare() // prepare queue

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
        mediaSession.controller.transportControls.playFromMediaId(MediaId.shuffleId().toString(), null)
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
        androidx.media.session.MediaButtonReceiver.handleIntent(mediaSession, intent)
    }

    override fun handleToggleFavorite() {
        callback.onSetRating(null)
    }

    override fun handleSleepTimerEnd(intent: Intent) {
        sleepTimerUseCase.reset()
        mediaSession.controller.transportControls.pause()
    }

    override fun handlePlayFromVoiceSearch(intent: Intent) {
        val voiceParams = intent.extras!!
        val query = voiceParams.getString(SearchManager.QUERY)!!
        callback.onPlayFromSearch(query, voiceParams)
    }

    override fun handlePlayFromUri(intent: Intent) {
        intent.data?.let { uri ->
            callback.onPlayFromUri(uri, null)
        }
    }

    override fun handleReplay10(intent: Intent) {
        mediaSession.controller.transportControls.sendCustomAction(
            MusicServiceAction.REPLAY_10.name, null
        )
    }

    override fun handleReplay30(intent: Intent) {
        mediaSession.controller.transportControls.sendCustomAction(
            MusicServiceAction.REPLAY_30.name, null
        )
    }

    override fun handleForward10(intent: Intent) {
        mediaSession.controller.transportControls.sendCustomAction(
            MusicServiceAction.FORWARD_10.name, null
        )
    }

    override fun handleForward30(intent: Intent) {
        mediaSession.controller.transportControls.sendCustomAction(
            MusicServiceAction.FORWARD_30.name, null
        )
    }

    private fun resetSleepTimer() {
        sleepTimerUseCase.reset()
        alarmManager.cancel(PendingIntents.stopMusicServiceIntent(this))
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        if (clientPackageName == packageName) {
            return BrowserRoot(MediaIdHelper.MEDIA_ID_ROOT, null)
        }

        if (CarHelper.isValidCarPackage(clientPackageName)) {
            val extras = Bundle()
            extras.putBoolean(CONTENT_STYLE_SUPPORTED, true)
//            extras.putBoolean(EXTRA_MEDIA_SEARCH_SUPPORTED, true) TODO not sure what is doing
            extras.putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_GRID_ITEM_HINT_VALUE)
            extras.putInt(CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_LIST_ITEM_HINT_VALUE)
            return BrowserRoot(MediaIdHelper.MEDIA_ID_ROOT, extras)
        }
        if (WearHelper.isValidWearCompanionPackage(clientPackageName)) {
            return BrowserRoot(MediaIdHelper.MEDIA_ID_ROOT, null)
        }

        return null
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        if (parentId == MediaIdHelper.MEDIA_ID_ROOT) {
            result.sendResult(MediaIdHelper.getLibraryCategories(this))
            return
        }
        result.detach()
        // TODO made cancellable
        launch(Dispatchers.Default) {

            val mediaIdCategory = MediaIdCategory.values()
                .toList()
                .find { it.toString() == parentId }

            val songList = if (mediaIdCategory != null) {
                mediaItemGenerator.get().getCategoryChilds(mediaIdCategory)

            } else {
                val mediaId = MediaId.fromString(parentId)
                mediaItemGenerator.get().getCategoryValueChilds(mediaId)
            }
            result.sendResult(songList)
        }
    }

    private fun buildMediaButtonReceiverPendingIntent(): PendingIntent {
        val intent = Intent(Intent.ACTION_MEDIA_BUTTON)
        intent.setClass(this, this.javaClass)
        return intent.asServicePendingIntent(this, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private fun buildSessionActivityPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(
            this, 0,
            Intent(this, Class.forName(Classes.ACTIVITY_MAIN)), PendingIntent.FLAG_CANCEL_CURRENT
        )
    }
}