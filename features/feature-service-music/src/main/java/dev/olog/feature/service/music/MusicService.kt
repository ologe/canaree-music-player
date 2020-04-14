package dev.olog.feature.service.music

import android.app.Activity
import android.app.PendingIntent
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.lifecycleScope
import androidx.media.session.MediaButtonReceiver
import dagger.Lazy
import dagger.android.AndroidInjection
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.interactor.SleepTimerUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.core.constants.MusicServiceCustomAction
import dev.olog.feature.service.music.helper.CarHelper
import dev.olog.feature.service.music.helper.CarHelper.CONTENT_STYLE_BROWSABLE_HINT
import dev.olog.feature.service.music.helper.CarHelper.CONTENT_STYLE_LIST_ITEM_HINT_VALUE
import dev.olog.feature.service.music.helper.CarHelper.CONTENT_STYLE_PLAYABLE_HINT
import dev.olog.feature.service.music.helper.CarHelper.CONTENT_STYLE_SUPPORTED
import dev.olog.feature.service.music.helper.MediaIdHelper
import dev.olog.feature.service.music.helper.MediaItemGenerator
import dev.olog.feature.service.music.helper.WearHelper
import dev.olog.feature.service.music.notification.MusicNotificationManager
import dev.olog.feature.service.music.scrobbling.LastFmScrobbling
import dev.olog.feature.service.music.state.MusicServiceMetadata
import dev.olog.navigation.screens.Activities
import dev.olog.shared.android.extensions.asServicePendingIntent
import kotlinx.coroutines.launch
import javax.inject.Inject

class MusicService : BaseMusicService() {

    companion object {
        @JvmStatic
        val TAG = "SM:${MusicService::class.java.simpleName}"
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
    internal lateinit var lastFmScrobbling: LastFmScrobbling
    @Inject
    internal lateinit var noisy: Noisy
    @Inject
    internal lateinit var schedulers: Schedulers
    @Inject
    lateinit var activities: Map<Activities, @JvmSuppressWildcards Class<out Activity>>

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        setupMediaSession()
    }

    private fun setupMediaSession(){
        sessionToken = mediaSession.sessionToken

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
        callback.onPlay()
    }

    override fun handleAppShortcutShuffle(intent: Intent) {
        callback.onPlayFromMediaId(MediaId.SHUFFLE_ID.toString(), null)
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

    override fun handleToggleFavorite() {
        callback.onCustomAction(MusicServiceCustomAction.TOGGLE_FAVORITE.name, null)
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
        callback.onCustomAction(MusicServiceCustomAction.REPLAY_10.name, null)
    }

    override fun handleReplay30(intent: Intent) {
        callback.onCustomAction(MusicServiceCustomAction.REPLAY_30.name, null)
    }

    override fun handleForward10(intent: Intent) {
        callback.onCustomAction(MusicServiceCustomAction.FORWARD_10.name, null)
    }

    override fun handleForward30(intent: Intent) {
        callback.onCustomAction(MusicServiceCustomAction.FORWARD_30.name, null)
    }

    private fun resetSleepTimer() {
        sleepTimerUseCase.reset()
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
            extras.putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_LIST_ITEM_HINT_VALUE)
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
        lifecycleScope.launch(schedulers.cpu) {

            val mediaIdCategory = MediaIdCategory.values()
                .toList()
                .find { it.toString() == parentId }

            val songList = if (mediaIdCategory != null) {
                mediaItemGenerator.get().getCategoryChilds(mediaIdCategory)

            } else {
                val mediaId = MediaId.fromString(parentId)
                mediaItemGenerator.get().getCategoryValueChilds(mediaId as MediaId.Category)
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
            Intent(this, activities[Activities.MAIN]), PendingIntent.FLAG_CANCEL_CURRENT
        )
    }
}