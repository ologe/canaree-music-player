package dev.olog.domain.interactor

import dev.olog.domain.entity.Song
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.*
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaId
import dev.olog.shared.MediaIdCategory
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

) : FlowableUseCaseWithParam<List<Song>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<List<Song>> {
        if (mediaId.isAll){
            return songDataStore.getAll()
        }

        return when (mediaId.category) {
            MediaIdCategory.FOLDER -> folderDataStore.observeSongListByParam(mediaId.categoryValue)
            MediaIdCategory.PLAYLIST -> playlistDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.SONGS -> songDataStore.getAll()
            MediaIdCategory.ALBUM -> albumDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.ARTIST -> artistDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.GENRE -> genreDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
            else -> throw AssertionError("invalid media id " + mediaId)
        }
    }
}
