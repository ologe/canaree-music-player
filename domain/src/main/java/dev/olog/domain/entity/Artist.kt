package dev.olog.domain.entity

data class Artist (
        val id: Long,
        val name: String,
        val image: String = ""
)