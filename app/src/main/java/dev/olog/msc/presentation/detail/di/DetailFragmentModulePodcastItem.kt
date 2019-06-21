package dev.olog.msc.presentation.detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.MediaIdCategoryKey
import dev.olog.msc.domain.entity.PodcastAlbum
import dev.olog.msc.domain.entity.PodcastArtist
import dev.olog.msc.domain.entity.PodcastPlaylist
import dev.olog.msc.domain.interactor.item.GetPodcastAlbumUseCase
import dev.olog.msc.domain.interactor.item.GetPodcastArtistUseCase
import dev.olog.msc.domain.interactor.item.GetPodcastPlaylistUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.shared.TextUtils
import dev.olog.msc.utils.k.extension.asFlowable
import io.reactivex.Flowable

@Module
class DetailFragmentModulePodcastItem {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_PLAYLIST)
    internal fun providePlaylistItem(
        resources: Resources,
        mediaId: MediaId,
        useCase: GetPodcastPlaylistUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem(resources) }
                .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ALBUMS)
    internal fun provideAlbumItem(
        mediaId: MediaId,
        useCase: GetPodcastAlbumUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem() }
                .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PODCASTS_ARTISTS)
    internal fun provideArtistItem(
        resources: Resources,
        mediaId: MediaId,
        useCase: GetPodcastArtistUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem(resources) }
                .asFlowable()
    }

}

private fun PodcastPlaylist.toHeaderItem(resources: Resources): List<DisplayableItem> {
    val listSize = if (this.size == -1){ "" } else {
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    }

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            MediaId.podcastPlaylistId(this.id),
            title,
            listSize,
            image = image
        )
    )

}

private fun PodcastAlbum.toHeaderItem(): List<DisplayableItem> {

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            MediaId.podcastAlbumId(this.id),
            title,
            DisplayableItem.adjustArtist(this.artist),
            image = image
        )
    )
}

private fun PodcastArtist.toHeaderItem(resources: Resources): List<DisplayableItem> {
    val songs = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(R.plurals.common_plurals_album, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            MediaId.podcastArtistId(this.id),
            name,
            "$albums$songs".toLowerCase(),
            image = image
        )
    )
}