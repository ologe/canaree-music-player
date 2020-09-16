package dev.olog.feature.library.sample

import androidx.ui.tooling.preview.datasource.LoremIpsum
import dev.olog.feature.library.track.TracksFragmentModel
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory.SONGS
import dev.olog.shared.TextUtils
import kotlin.random.Random

internal object TrackModelProvider {

    val data = listOf(
        TracksFragmentModel.Shuffle,
        *(0..10).map {
            val mediaId = PresentationId.Track(SONGS, "", it.toString())
            val title = LoremIpsum(Random.nextInt(3, 10)).values.joinToString()
            val artist = LoremIpsum(Random.nextInt(1, 3)).values.joinToString()
            val album = LoremIpsum(Random.nextInt(1, 3)).values.joinToString()
            val subtitle = TextUtils.buildSubtitle(artist, album)
            TracksFragmentModel.Track(mediaId, title, subtitle)
        }.toTypedArray(),
    )

}