package dev.olog.service.music.scrobbling

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import de.umass.lastfm.Authenticator
import de.umass.lastfm.Caller
import de.umass.lastfm.Session
import de.umass.lastfm.Track
import de.umass.lastfm.scrobble.ScrobbleData
import dev.olog.core.entity.UserCredentials
import dev.olog.core.interactor.ObserveLastFmUserCredentials
import dev.olog.service.music.BuildConfig
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.player.InternalPlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.util.logging.Level
import javax.inject.Inject
import kotlin.time.seconds

@ServiceScoped
internal class LastFmScrobbling @Inject constructor(
    lifecycleOwner: LifecycleOwner,
    observeLastFmUserCredentials: ObserveLastFmUserCredentials,
    playerState: InternalPlayerState,
) {

    companion object {
        private val SCROBBLE_DELAY = 10.seconds
    }

    private var session: Session? = null
    private var userCredentials: UserCredentials? = null

    init {
        Caller.getInstance().userAgent = "dev.olog.msc"
        Caller.getInstance().logger.level = Level.OFF

        playerState.state
            .map { it.entity }
            .distinctUntilChanged()
            .mapLatest(this::scrobble)
            .launchIn(lifecycleOwner.lifecycleScope)

        observeLastFmUserCredentials()
            .filter { it.username.isNotBlank() }
            .onEach(this::tryAuthenticate)
            .flowOn(Dispatchers.IO)
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    private fun tryAuthenticate(credentials: UserCredentials) {
        try {
            session = Authenticator.getMobileSession(
                credentials.username,
                credentials.password,
                BuildConfig.LAST_FM_KEY,
                BuildConfig.LAST_FM_SECRET
            )
            userCredentials = credentials
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    private suspend fun scrobble(entity: MediaEntity){
        if (session == null || userCredentials == null){
            return
        }

        delay(SCROBBLE_DELAY)
        val scrobbleData = entity.toScrollData()
        Track.scrobble(scrobbleData, session)
        Track.updateNowPlaying(scrobbleData, session)
    }

}

private fun MediaEntity.toScrollData(): ScrobbleData {
    val musicBrainzId = try {
        val audioFile = AudioFileIO.read(File(this.path))
        audioFile.tagAndConvertOrCreateAndSetDefault
        val tag = audioFile.tag
        tag.getFirst(FieldKey.MUSICBRAINZ_TRACK_ID)
    } catch (ex: Throwable){
        null
    }

    return ScrobbleData(
        this.artist,
        this.title,
        (System.currentTimeMillis() / 1000).toInt(),
        this.duration.toInt(),
        this.album,
        null,
        musicBrainzId,
        this.trackNumber,
        null,
        true
    )
}