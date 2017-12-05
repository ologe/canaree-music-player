package dev.olog.domain.entity

data class Artist (
        val id: Long,
        val name: String,
        val songs: Int,
        val albums: Int,
        val image: String = ""
)