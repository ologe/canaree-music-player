package dev.olog.feature.edit.model

import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.lib.audio.tagger.model.Tags

data class UpdateArtistInfo(
    val mediaId: PresentationId.Category,
    val tags: Tags,
    val isPodcast: Boolean
)