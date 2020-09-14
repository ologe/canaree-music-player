package dev.olog.feature.library.sample

import androidx.ui.tooling.preview.PreviewParameterProvider
import androidx.ui.tooling.preview.datasource.LoremIpsum
import dev.olog.feature.library.artist.ArtistFragmentModel
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory.ARTISTS
import kotlin.random.Random

internal class ArtistModelProvider : PreviewParameterProvider<List<ArtistFragmentModel>> {

    companion object {
        val data = listOf(
            *(0..10).map {
                val mediaId = PresentationId.Category(ARTISTS, it.toString())
                val title = LoremIpsum(Random.nextInt(3, 10)).values.joinToString()
                ArtistFragmentModel(mediaId, title, "5 songs")
            }.toTypedArray(),
        )
    }

    override val values: Sequence<List<ArtistFragmentModel>>
        get() = sequenceOf(data)
}