package dev.olog.presentation._base

import dev.olog.shared.MediaId

interface BaseModel {
    val type: Int
    val mediaId: MediaId
}