package dev.olog.domain.entity

data class Genre (
        val id: Long,
        val name: String,
        val size: Int = -1,
        val image: String = ""
)