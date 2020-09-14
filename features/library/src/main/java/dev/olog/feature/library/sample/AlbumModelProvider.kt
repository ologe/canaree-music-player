package dev.olog.feature.library.sample

import androidx.ui.tooling.preview.PreviewParameterProvider
import androidx.ui.tooling.preview.datasource.LoremIpsum
import dev.olog.feature.library.album.AlbumFragmentModel
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory.ALBUMS
import dev.olog.shared.TextUtils
import kotlin.random.Random

internal class AlbumModelProvider : PreviewParameterProvider<List<AlbumFragmentModel>> {

    companion object {
        val data = listOf(
            *(0..10).map {
                val mediaId = PresentationId.Category(ALBUMS, it.toString())
                val title = LoremIpsum(Random.nextInt(3, 10)).values.joinToString()
                val artist = LoremIpsum(Random.nextInt(1, 3)).values.joinToString()
                val album = LoremIpsum(Random.nextInt(1, 3)).values.joinToString()
                val subtitle = TextUtils.buildSubtitle(artist, album)
                AlbumFragmentModel(mediaId, title, subtitle)
            }.toTypedArray(),
        )
    }

    override val values: Sequence<List<AlbumFragmentModel>>
        get() = sequenceOf(data)
}