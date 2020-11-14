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
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.MetadataEntity
import dev.olog.shared.android.coroutine.autoDisposeJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.util.logging.Level
import javax.inject.Inject
import kotlin.time.milliseconds

@ServiceScoped
internal class LastFmScrobbling @Inject constructor(
    private val lifecycleOwner: LifecycleOwner,
    observeLastFmUserCredentials: ObserveLastFmUserCredentials,
    playerLifecycle: IPlayerLifecycle,

) : IPlayerLifecycle.Listener {

    companion object {
        private val SCROBBLE_DELAY = 10.milliseconds
    }

    private var session: Session? = null
    private var userCredentials: UserCredentials? = null

    private var scrobbleJob by autoDisposeJob()

    init {
        playerLifecycle.addListener(this)

        Caller.getInstance().userAgent = "dev.olog.msc"
        Caller.getInstance().logger.level = Level.OFF

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

    override fun onMetadataChanged(metadata: MetadataEntity) {
        scrobble(metadata.entity)
    }

    private fun scrobble(entity: MediaEntity){
        if (session == null || userCredentials == null){
            return
        }

        scrobbleJob = lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            delay(SCROBBLE_DELAY)
            val scrobbleData = entity.toScrollData()
            Track.scrobble(scrobbleData, session)
            Track.updateNowPlaying(scrobbleData, session)
        }
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