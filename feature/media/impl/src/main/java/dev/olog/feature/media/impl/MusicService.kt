package dev.olog.feature.media.impl

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
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.feature.media.api.interactor.SleepTimerUseCase
import dev.olog.feature.media.impl.notification.MusicNotificationManager
import dev.olog.feature.media.impl.scrobbling.LastFmScrobbling
import dev.olog.feature.media.impl.state.MusicServiceMetadata
import dev.olog.feature.media.api.MusicServiceCustomAction
import dev.olog.core.PendingIntentFactory
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.media.impl.helper.CarHelper
import dev.olog.feature.media.impl.helper.CarHelper.CONTENT_STYLE_BROWSABLE_HINT
import dev.olog.feature.media.impl.helper.CarHelper.CONTENT_STYLE_LIST_ITEM_HINT_VALUE
import dev.olog.feature.media.impl.helper.CarHelper.CONTENT_STYLE_PLAYABLE_HINT
import dev.olog.feature.media.impl.helper.CarHelper.CONTENT_STYLE_SUPPORTED
import dev.olog.feature.media.impl.helper.MediaIdHelper
import dev.olog.feature.media.impl.helper.MediaItemGenerator
import dev.olog.feature.media.impl.helper.WearHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : BaseMusicService() {

    companion object {
        val TAG = "SM:${MusicService::class.java.simpleName}"
    }

    @Inject
    lateinit var mediaSession: MediaSessionCompat
    @Inject
    lateinit var callback: MediaSessionCallback

    @Inject
    lateinit var currentSong: CurrentSong
    @Inject
    lateinit var playerMetadata: MusicServiceMetadata
    @Inject
    lateinit var notification: MusicNotificationManager
    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase
    @Inject
    lateinit var mediaItemGenerator: Lazy<MediaItemGenerator>
    @Inject
    lateinit var lastFmScrobbling: LastFmScrobbling
    @Inject
    lateinit var noisy: Noisy
    @Inject
    lateinit var pendingIntentFactory: PendingIntentFactory
    @Inject
    lateinit var featureMainNavigator: FeatureMainNavigator

    override fun onCreate() {
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
        callback.onPlayFromMediaId(MediaId.shuffleId().toString(), null)
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
        lifecycleScope.launch(Dispatchers.Default) {

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
        return pendingIntentFactory.createForService(intent)
    }

    private fun buildSessionActivityPendingIntent(): PendingIntent {
        val intent = featureMainNavigator.createContentViewIntent()
        return pendingIntentFactory.createForActivity(intent)
    }
}