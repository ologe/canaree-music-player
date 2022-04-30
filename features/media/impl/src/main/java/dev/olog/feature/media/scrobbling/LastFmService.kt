package dev.olog.feature.media.scrobbling

import de.umass.lastfm.Authenticator
import de.umass.lastfm.Caller
import de.umass.lastfm.Session
import de.umass.lastfm.Track
import de.umass.lastfm.scrobble.ScrobbleData
import dev.olog.core.Config
import dev.olog.core.ServiceScope
import dev.olog.core.entity.UserCredentials
import dev.olog.core.schedulers.Schedulers
import dev.olog.feature.media.model.MediaEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.util.logging.Level
import javax.inject.Inject

internal class LastFmService @Inject constructor(
    private val config: Config,
    private val schedulers: Schedulers,
    private val serviceScope: ServiceScope,
) {

    companion object {
        const val SCROBBLE_DELAY = 10L * 1000 // millis
    }

    private var session: Session? = null
    private var userCredentials: UserCredentials? = null

    private var scrobbleJob: Job? = null

    init {
        Caller.getInstance().userAgent = config.appId
        Caller.getInstance().logger.level = Level.OFF
    }

    suspend fun tryAuthenticate(credentials: UserCredentials) = withContext(schedulers.io) {
        try {
            session = Authenticator.getMobileSession(
                credentials.username,
                credentials.password,
                config.lastFmKey,
                config.lastFmSecret,
            )
            userCredentials = credentials
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    fun scrobble(entity: MediaEntity){
        if (session == null || userCredentials == null){
            return
        }

        scrobbleJob?.cancel()
        scrobbleJob = serviceScope.launch {
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

}