package dev.olog.msc.domain.interactor.albums.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.dagger.MediaIdCategoryKey
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.interactor.albums.AlbumsFragment
import dev.olog.msc.domain.interactor.albums.AlbumsFragmentViewModel
import dev.olog.msc.domain.interactor.albums.AlbumsFragmentViewModelFactory
import dev.olog.msc.domain.interactor.detail.siblings.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.groupMap
import io.reactivex.Flowable

@Module
class AlbumsFragmentModule(
       private val fragment: AlbumsFragment
) {

    @Provides
    @FragmentLifecycle
    internal fun lifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(AlbumsFragment.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    internal fun provideViewModel(factory: AlbumsFragmentViewModelFactory): AlbumsFragmentViewModel {

        return ViewModelProviders.of(fragment, factory).get(AlbumsFragmentViewModel::class.java)
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.FOLDER)
    internal fun provideFolderData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetFolderSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toAlbumsDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PLAYLIST)
    internal fun providePlaylistData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetPlaylistSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toAlbumsDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUM)
    internal fun provideAlbumData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetAlbumSiblingsByAlbumUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toAlbumsDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTIST)
    internal fun provideArtistData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetAlbumSiblingsByArtistUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toAlbumsDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRE)
    internal fun provideGenreData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetGenreSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toAlbumsDetailDisplayableItem(resources) }
    }
}


private fun Folder.toAlbumsDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_albums,
            MediaId.folderId(path),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image
    )
}

private fun Playlist.toAlbumsDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_albums,
            MediaId.playlistId(id),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image
    )
}

private fun Album.toAlbumsDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_albums,
            MediaId.albumId(id),
            title,
            resources.getQuantityString(R.plurals.song_count, this.songs, this.songs).toLowerCase(),
            image
    )
}

private fun Genre.toAlbumsDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_albums,
            MediaId.genreId(id),
            name.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image
    )
}
