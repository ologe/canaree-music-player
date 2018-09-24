package dev.olog.msc.domain.entity

data class Playlist (
        val id: Long,
        val title: String,
        val size: Int,
        val image: String,
        val type: PlaylistType
)