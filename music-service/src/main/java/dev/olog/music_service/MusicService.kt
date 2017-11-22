package dev.olog.music_service

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class MusicService : BaseMusicService() {

    companion object {
        const val TAG = "MusicService"
    }

    @Inject lateinit var activityClass : ActivityClass
    @Inject lateinit var mediaSession: MediaSessionCompat
    @Inject lateinit var callback: MediaSessionCallback

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
        mediaSession.setMediaButtonReceiver(null)
        mediaSession.setCallback(null)
        mediaSession.isActive = false
        mediaSession.release()
    }

    override fun handleMediaButton(intent: Intent) {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(MediaIdHelper.MEDIA_ID_ROOT, null)
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