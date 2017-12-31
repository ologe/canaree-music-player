package dev.olog.presentation.fragment_albums.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.domain.entity.*
import dev.olog.domain.interactor.GetSmallPlayType
import dev.olog.domain.interactor.detail.siblings.*
import dev.olog.presentation.R
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_albums.AlbumsFragment
import dev.olog.presentation.fragment_albums.AlbumsFragmentViewModel
import dev.olog.presentation.fragment_albums.AlbumsFragmentViewModelFactory
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaId
import dev.olog.shared.MediaIdCategory
import dev.olog.shared.MediaIdCategoryKey
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables

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
            useCase: GetFolderSiblingsUseCase,
            getSmallPlayType: GetSmallPlayType): Flowable<List<DisplayableItem>> {

        return Flowables.combineLatest(
                useCase.execute(mediaId), getSmallPlayType.execute(), { data, smallPlayType ->
            data.map { it.toAlbumsDetailDisplayableItem(resources, smallPlayType) }
        })
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PLAYLIST)
    internal fun providePlaylistData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetPlaylistSiblingsUseCase,
            getSmallPlayType: GetSmallPlayType): Flowable<List<DisplayableItem>> {

        return Flowables.combineLatest(
                useCase.execute(mediaId), getSmallPlayType.execute(), { data, smallPlayType ->
            data.map { it.toAlbumsDetailDisplayableItem(resources, smallPlayType) }
        })
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUM)
    internal fun provideAlbumData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetAlbumSiblingsByAlbumUseCase,
            getSmallPlayType: GetSmallPlayType): Flowable<List<DisplayableItem>> {

        return Flowables.combineLatest(
                useCase.execute(mediaId), getSmallPlayType.execute(), { data, smallPlayType ->
            data.map { it.toAlbumsDetailDisplayableItem(resources, smallPlayType) }
        })
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTIST)
    internal fun provideArtistData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetAlbumSiblingsByArtistUseCase,
            getSmallPlayType: GetSmallPlayType): Flowable<List<DisplayableItem>> {

        return Flowables.combineLatest(
                useCase.execute(mediaId), getSmallPlayType.execute(), { data, smallPlayType ->
            data.map { it.toAlbumsDetailDisplayableItem(resources, smallPlayType) }
        })
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRE)
    internal fun provideGenreData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetGenreSiblingsUseCase,
            getSmallPlayType: GetSmallPlayType): Flowable<List<DisplayableItem>> {

        return Flowables.combineLatest(
                useCase.execute(mediaId), getSmallPlayType.execute(), { data, smallPlayType ->
            data.map { it.toAlbumsDetailDisplayableItem(resources, smallPlayType) }
        })
    }
}


private fun Folder.toAlbumsDetailDisplayableItem(resources: Resources, smallPlayType: SmallPlayType): DisplayableItem {
    return DisplayableItem(
            R.layout.item_albums,
            MediaId.folderId(path),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image,
            smallPlayType = smallPlayType
    )
}

private fun Playlist.toAlbumsDetailDisplayableItem(resources: Resources, smallPlayType: SmallPlayType): DisplayableItem {
    return DisplayableItem(
            R.layout.item_albums,
            MediaId.playlistId(id),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image,
            smallPlayType = smallPlayType
    )
}

private fun Album.toAlbumsDetailDisplayableItem(resources: Resources, smallPlayType: SmallPlayType): DisplayableItem {
    return DisplayableItem(
            R.layout.item_albums,
            MediaId.albumId(id),
            title,
            resources.getQuantityString(R.plurals.song_count, this.songs, this.songs).toLowerCase(),
            image,
            smallPlayType = smallPlayType
    )
}

private fun Genre.toAlbumsDetailDisplayableItem(resources: Resources, smallPlayType: SmallPlayType): DisplayableItem {
    return DisplayableItem(
            R.layout.item_albums,
            MediaId.genreId(id),
            name.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image,
            smallPlayType = smallPlayType
    )
}
