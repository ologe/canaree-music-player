package dev.olog.core.sort

import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import javax.inject.Inject

class SetDetailSortUseCase @Inject constructor(

) {

    @Suppress("UNCHECKED_CAST") // TODO test carefully
    operator fun invoke(
        category: MediaUri.Category,
        type: MediaStoreType,
        sort: Sort<SortType>
    ) {
        return when (category) {
            MediaUri.Category.Folder -> TODO()
            MediaUri.Category.Playlist -> TODO()
            MediaUri.Category.Track -> TODO()
            MediaUri.Category.Author -> TODO()
            MediaUri.Category.Collection -> TODO()
            MediaUri.Category.Genre -> TODO()
        }
//        return when (mediaId.category) {
//            MediaIdCategory.FOLDERS -> folderGateway.setDetailSort(sort as Sort<FolderDetailSort>)
//            MediaIdCategory.PLAYLISTS -> playlistGateway.setDetailSort(sort as Sort<PlaylistDetailSort>)
//            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.setDetailSort(sort as Sort<PlaylistDetailSort>)
//            MediaIdCategory.ALBUMS -> albumGateway.setDetailSort(sort as Sort<CollectionDetailSort>)
//            MediaIdCategory.PODCASTS_ALBUMS -> podcastCollectionGateway.setDetailSort(sort as Sort<CollectionDetailSort>)
//            MediaIdCategory.ARTISTS -> artistGateway.setDetailSort(sort as Sort<AuthorDetailSort>)
//            MediaIdCategory.PODCASTS_ARTISTS -> podcastAuthorGateway.setDetailSort(sort as Sort<AuthorDetailSort>)
//            MediaIdCategory.GENRES -> genreGateway.setDetailSort(sort as Sort<GenreDetailSort>)
//            MediaIdCategory.SONGS,
//            MediaIdCategory.PODCASTS,
//            MediaIdCategory.HEADER,
//            MediaIdCategory.PLAYING_QUEUE -> error("invalid media id=$mediaId")
//        }
    }
}