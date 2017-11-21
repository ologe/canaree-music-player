package dev.olog.domain.entity

data class Playlist (
        val id: Long,
        val title: String,
        val image: String = ""
)