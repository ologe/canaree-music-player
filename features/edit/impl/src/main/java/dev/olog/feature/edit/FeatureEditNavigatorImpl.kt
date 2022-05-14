package dev.olog.feature.edit

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import dev.olog.feature.edit.album.EditAlbumFragment
import dev.olog.feature.edit.api.FeatureEditNavigator
import dev.olog.feature.edit.artist.EditArtistFragment
import dev.olog.feature.edit.song.EditTrackFragment
import dev.olog.platform.allowed
import dev.olog.shared.extension.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class FeatureEditNavigatorImpl @Inject constructor(
    private val getSongUseCase: SongGateway,
    private val getPodcastUseCase: PodcastGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase
) : FeatureEditNavigator {

    override fun toEditInfoFragment(activity: FragmentActivity, mediaId: MediaId) {
        if (!allowed()) {
            return
        }
        activity.lifecycleScope.launchWhenResumed {
            when {
                mediaId.isLeaf -> {
                    toEditTrack(activity, mediaId) {
                        val instance = EditTrackFragment.newInstance(mediaId)
                        instance.show(activity.supportFragmentManager, EditTrackFragment.TAG)
                    }
                }
                mediaId.isAlbum || mediaId.isPodcastAlbum -> {
                    toEditAlbum(activity, mediaId) {
                        val instance = EditAlbumFragment.newInstance(mediaId)
                        instance.show(activity.supportFragmentManager, EditAlbumFragment.TAG)
                    }
                }
                mediaId.isArtist || mediaId.isPodcastArtist -> {
                    toEditArtist(activity, mediaId) {
                        val instance = EditArtistFragment.newInstance(mediaId)
                        instance.show(activity.supportFragmentManager, EditArtistFragment.TAG)
                    }
                }
                else -> throw IllegalArgumentException("invalid media id $mediaId")
            }
        }
    }

    private suspend fun toEditTrack(
        activity: FragmentActivity,
        mediaId: MediaId,
        action: () -> Unit,
    ) = withContext(Dispatchers.IO) {
        try {
            if (mediaId.isAnyPodcast) {
                val song = getPodcastUseCase.getByParam(mediaId.resolveId)!!
                checkItem(song)
            } else {
                val song = getSongUseCase.getByParam(mediaId.resolveId)!!
                checkItem(song)
            }
            withContext(Dispatchers.Main) {
                action()
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            withContext(Dispatchers.Main) {
                showError(activity, ex)
            }
        }
    }

    private suspend fun toEditAlbum(
        activity: FragmentActivity,
        mediaId: MediaId,
        action: () -> Unit,
    ) = withContext(Dispatchers.IO) {
        try {
            getSongListByParamUseCase.invoke(mediaId).forEach { checkItem(it) }
            withContext(Dispatchers.Main) {
                action()
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            withContext(Dispatchers.Main) {
                showError(activity, ex)
            }
        }
    }

    private suspend fun toEditArtist(
        activity: FragmentActivity,
        mediaId: MediaId,
        action: () -> Unit,
    ) = withContext(Dispatchers.IO) {
        try {
            getSongListByParamUseCase.invoke(mediaId).forEach { checkItem(it) }
            withContext(Dispatchers.Main) {
                action()
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            withContext(Dispatchers.Main) {
                showError(activity, ex)
            }
        }
    }

    private fun checkItem(song: Song) {
        val file = File(song.path)
        val audioFile = AudioFileIO.read(file)
        audioFile.tagOrCreateAndSetDefault
    }

    private fun showError(context: Context, error: Throwable) {
        when (error) {
            is CannotReadException -> context.toast(R.string.edit_song_error_can_not_read)
            is IOException -> context.toast(R.string.edit_song_error_io)
            is ReadOnlyFileException -> context.toast(R.string.edit_song_error_read_only)
            else -> {
                error.printStackTrace()
                context.toast(R.string.edit_song_error)
            }
        }
    }

}