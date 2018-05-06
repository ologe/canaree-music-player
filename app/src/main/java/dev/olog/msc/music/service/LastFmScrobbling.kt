package dev.olog.msc.music.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.support.v4.media.session.PlaybackStateCompat
import de.umass.lastfm.Authenticator
import de.umass.lastfm.Caller
import de.umass.lastfm.Session
import de.umass.lastfm.Track
import de.umass.lastfm.scrobble.ScrobbleData
import dev.olog.msc.api.last.fm.LAST_FM_API_KEY
import dev.olog.msc.api.last.fm.LAST_FM_API_SECRET
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.domain.entity.UserCredendials
import dev.olog.msc.domain.interactor.scrobble.ObserveLastFmUserCredentials
import dev.olog.msc.music.service.interfaces.PlayerLifecycle
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class LastFmScrobbling @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        observeLastFmUserCredentials: ObserveLastFmUserCredentials,
        playerLifecycle: PlayerLifecycle

) : DefaultLifecycleObserver, PlayerLifecycle.Listener {

    private var session: Session? = null
    private var userCredentials : UserCredendials? = null

    private val credendialsDisposable = observeLastFmUserCredentials.execute()
            .filter { it.username.isNotBlank() }
            .subscribe({
                session = Authenticator.getMobileSession(it.username, it.password, LAST_FM_API_KEY, LAST_FM_API_SECRET)
                userCredentials = it
            }, Throwable::printStackTrace)

    private var scrobbleSubscriptions = CompositeDisposable()

    init {
        lifecycle.addObserver(this)
        playerLifecycle.addListener(this)

        Caller.getInstance().userAgent = "dev.olog.msc"
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