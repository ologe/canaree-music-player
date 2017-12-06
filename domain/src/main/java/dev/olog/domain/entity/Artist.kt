package dev.olog.domain.entity

data class Artist (
        val id: Long,
        val name: String,
        val songs: Int = -1,
        val albums: Int = -1,
        val image: String = ""
)