package dev.olog.data.model

import dev.olog.data.model.deezer.DeezerDataTrack

data class DeezerTrackResponse(
    val data: List<DeezerDataTrack>
)

