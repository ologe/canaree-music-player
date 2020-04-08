package dev.olog.lib.model

import dev.olog.lib.model.deezer.DeezerDataTrack

data class DeezerTrackResponse(
    val data: List<DeezerDataTrack>
)

