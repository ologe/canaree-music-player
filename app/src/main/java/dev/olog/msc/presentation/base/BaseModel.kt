package dev.olog.msc.presentation.base

import dev.olog.msc.utils.MediaId

interface BaseModel {
    val type: Int
    val mediaId: MediaId
}