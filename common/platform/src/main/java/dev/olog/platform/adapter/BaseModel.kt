package dev.olog.platform.adapter

import dev.olog.core.MediaId

interface BaseModel {
    val type: Int
    val mediaId: MediaId
}