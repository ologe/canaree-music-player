package dev.olog.service.music.scrobbling

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import de.umass.lastfm.Authenticator
import de.umass.lastfm.Caller
import de.umass.lastfm.Session
import de.umass.lastfm.Track
import de.umass.lastfm.scrobble.ScrobbleData
import dev.olog.core.entity.UserCredentials
import dev.olog.core.interactor.ObserveLastFmUserCredentials
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.service.music.BuildConfig
import dev.olog.service.music.interfaces.PlayerLifecycle
import dev.olog.service.music.model.MediaEntity
import dev.olog.shared.extensions.unsubscribe
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.util.logging.Level
import javax.inject.Inject

class LastFmScrobbling @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    observeLastFmUserCredentials: ObserveLastFmUserCredentials,
    playerLifecycle: PlayerLifecycle

) : DefaultLifecycleObserver, PlayerLifecycle.Listener {

    private var session: Session? = null
    private var userCredentials : UserCredentials? = null

    private val credendialsDisposable = observeLastFmUserCredentials.execute()
            .observeOn(Schedulers.io())
            .filter { it.username.isNotBlank() }
            .subscribe(this::tryAutenticate, Throwable::printStackTrace)

    private fun tryAutenticate(credentials: UserCredentials){
        try {
            session = Authenticator.getMobileSession(credentials.username, credentials.password,
                BuildConfig.LAST_FM_KEY,
                BuildConfig.LAST_FM_SECRET
            )
            userCredentials = credentials
        } catch (ex: Exception){
            ex.printStackTrace()
        }
    }

    private var scrobbleSubscriptions = CompositeDisposable()

    init {
        lifecycle.addObserver(this)
        playerLifecycle.addListener(this)

        Caller.getInstance().userAgent = "dev.olog.msc"
        Caller.getInstance().logger.level = Level.OFF
    }

    override fun onDestroy(owner: LifecycleOwner) {
        credendialsDisposable.unsubscribe()
    }

    override fun onMetadataChanged(entity: MediaEntity) {
        scrobble(entity)
    }

    private fun scrobble(entity: MediaEntity){
        Single.just(true)
                .observeOn(Schedulers.io())
                .filter { session != null && userCredentials != null }
                .flatMap { Maybe.fromCallable {
                    val scrobbleData = entity.toScrollData()
                    Track.scrobble(scrobbleData, session)
                    Track.updateNowPlaying(scrobbleData, session)
                } }
                .subscribe({ }, Throwable::printStackTrace)
                .addTo(scrobbleSubscriptions)
    }

    private fun MediaEntity.toScrollData(): ScrobbleData {
        val musicBrainzId = try {
            val audioFile = AudioFileIO.read(File(this.path))
            audioFile.tagAndConvertOrCreateAndSetDefault
            val tag = audioFile.tag
            tag.getFirst(FieldKey.MUSICBRAINZ_TRACK_ID)
        } catch (ex: Exception){
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