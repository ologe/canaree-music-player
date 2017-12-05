package dev.olog.domain.entity

data class Folder (
        val title: String,
        val path: String,
        val size: Int,
        val image: String = ""
)