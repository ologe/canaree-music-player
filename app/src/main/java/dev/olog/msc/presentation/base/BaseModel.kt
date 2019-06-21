package dev.olog.msc.presentation.base

import dev.olog.core.MediaId

interface BaseModel {
    val type: Int
    val mediaId: MediaId
}