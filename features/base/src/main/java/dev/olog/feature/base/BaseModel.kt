package dev.olog.feature.base

import dev.olog.core.MediaId

interface BaseModel {
    val type: Int
    val mediaId: MediaId
}