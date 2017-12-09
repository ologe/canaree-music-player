package dev.olog.domain.entity

import java.io.File

data class Folder (
        val title: String,
        val path: String,
        val size: Int,
        val image: String = path.replace(File.separator, "")
)