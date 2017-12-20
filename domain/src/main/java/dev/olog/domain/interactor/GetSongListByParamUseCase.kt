package dev.olog.domain.interactor

import dev.olog.domain.entity.Song
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.*
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import javax.inject.Inject


class GetSongListByParamUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val genreDataStore: GenreGateway,
        private val playlistDataStore: PlaylistGateway,
        private val albumDataStore: AlbumGateway,
        private val artistDataStore: ArtistGateway,
        private val folderDataStore: FolderGateway,
        private val songDataStore: SongGateway

) : FlowableUseCaseWithParam<List<Song>, String>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: String): Flowable<List<Song>> {
        val category = MediaIdHelper.extractCategory(mediaId)
        if (category == MediaIdHelper.MEDIA_ID_BY_ALL){
            return songDataStore.getAll()
        }
        val categoryValue = MediaIdHelper.extractCategoryValue(mediaId)

        when (category) {
            MediaIdHelper.MEDIA_ID_BY_GENRE -> return genreDataStore.observeSongListByParam(categoryValue.toLong())
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> return playlistDataStore.observeSongListByParam(categoryValue.toLong())
            MediaIdHelper.MEDIA_ID_BY_FOLDER -> return folderDataStore.observeSongListByParam(categoryValue)
            MediaIdHelper.MEDIA_ID_BY_ALBUM -> return albumDataStore.observeSongListByParam(categoryValue.toLong())
            MediaIdHelper.MEDIA_ID_BY_ARTIST -> return artistDataStore.observeSongListByParam(categoryValue.toLong())
            MediaIdHelper.MEDIA_ID_BY_ALL -> return songDataStore.getAll()
        }
        throw AssertionError("invalid media id " + mediaId)
    }
}
