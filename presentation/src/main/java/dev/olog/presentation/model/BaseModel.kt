package dev.olog.presentation.model

import dev.olog.presentation.PresentationId

interface BaseModel {
    val type: Int
    val mediaId: PresentationId
}