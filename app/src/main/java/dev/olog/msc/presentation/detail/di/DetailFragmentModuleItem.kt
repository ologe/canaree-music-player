package dev.olog.msc.presentation.detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.MediaIdCategoryKey
import dev.olog.msc.domain.entity.*
import dev.olog.msc.domain.interactor.detail.item.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.asFlowable
import dev.olog.msc.utils.k.extension.negate
import io.reactivex.Flowable

@Module
class DetailFragmentModuleItem {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.FOLDERS)
    internal fun provideFolderItem(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetFolderUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem(resources) }
                .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PLAYLISTS)
    internal fun providePlaylistItem(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetPlaylistUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem(resources) }
                .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUMS)
    internal fun provideAlbumItem(
            mediaId: MediaId,
            useCase: GetAlbumUseCase,
            artistUseCase: GetArtistUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .flatMap {album ->
                    artistUseCase.execute(MediaId.artistId(album.artistId))
                            .map { album.toHeaderItem(it) }
                }.asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTISTS)
    internal fun provideArtistItem(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetArtistUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem(resources) }
                .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRES)
    internal fun provideGenreItem(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetGenreUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem(resources) }
                .asFlowable()
    }
}


private fun Folder.toHeaderItem(resources: Resources): List<DisplayableItem> {

    return listOf(
            DisplayableItem(
                    R.layout.item_detail_item_image,
                    MediaId.folderId(path),
                    "",
                    image = image
            ),
            DisplayableItem(
                    R.layout.item_detail_item_info,
                    MediaId.headerId("item info"),
                    title,
                    resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
            )
    )
}

private fun Playlist.toHeaderItem(resources: Resources): List<DisplayableItem> {
    val listSize = if (this.size == -1){ "" } else {
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    }

    return listOf(
            DisplayableItem(
                    R.layout.item_detail_item_image,
                    MediaId.playlistId(this.id),
                    "",
                    image = image
            ),
            DisplayableItem(
                    R.layout.item_detail_item_info,
                    MediaId.headerId("item info"),
                    title,
                    listSize
            )
    )

}

private fun Album.toHeaderItem(artist: Artist): List<DisplayableItem> {

    return listOf(
            DisplayableItem(
                    R.layout.item_detail_item_image,
                    MediaId.albumId(this.id),
                    "",
                    image = image
            ),
            // manage carefully because contains an invalid media id
            DisplayableItem(
                    R.layout.item_detail_item_info,
                    MediaId.albumId(artist.id.negate()),
                    title,
                    this.artist,
                    artist.image
            )
    )
}

private fun Artist.toHeaderItem(resources: Resources): List<DisplayableItem> {
    val songs = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(R.plurals.common_plurals_album, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return listOf(
            DisplayableItem(
                    R.layout.item_detail_item_image,
                    MediaId.artistId(this.id),
                    "",
                    image = image
            ),
            DisplayableItem(
                    R.layout.item_detail_item_info,
                    MediaId.headerId("item info"),
                    name,
                    "$albums$songs".toLowerCase()
            )
    )
}

private fun Genre.toHeaderItem(resources: Resources): List<DisplayableItem> {

    return listOf(
            DisplayableItem(
                    R.layout.item_detail_item_image,
                    MediaId.genreId(this.id),
                    "",
                    image = image
            ),
            DisplayableItem(
                    R.layout.item_detail_item_info,
                    MediaId.headerId("item info"),
                    name,
                    resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
            )
    )
}