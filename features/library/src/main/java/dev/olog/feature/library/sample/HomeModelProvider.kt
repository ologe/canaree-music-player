package dev.olog.feature.library.sample

import androidx.ui.tooling.preview.datasource.LoremIpsum
import dev.olog.feature.library.home.HomeFragmentModel
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory
import kotlin.random.Random

internal object HomeModelProvider {

    fun items(category: PresentationIdCategory) = (0..6)
        .map {
            val title = LoremIpsum(Random.nextInt(3, 10)).values.joinToString()
            val subtitle = LoremIpsum(Random.nextInt(1, 3)).values.joinToString()
            HomeFragmentModel.Item(
                mediaId = PresentationId.Category(category, it.toString()),
                title = title,
                subtitle = subtitle
            )
        }

    val data = listOf(
        HomeFragmentModel.Header("Recently played"),
        HomeFragmentModel.RecentlyPlayedAlbums(items(PresentationIdCategory.ALBUMS)),
        HomeFragmentModel.RecentlyPlayedArtists(items(PresentationIdCategory.ARTISTS)),
        HomeFragmentModel.Header("Recently added"),
        HomeFragmentModel.RecentlyAddedAlbums(items(PresentationIdCategory.ALBUMS)),
        HomeFragmentModel.RecentlyAddedArtists(items(PresentationIdCategory.ARTISTS)),
    )

}