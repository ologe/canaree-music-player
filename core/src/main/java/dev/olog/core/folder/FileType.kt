package dev.olog.core.folder

import java.io.File

sealed interface FileType {

    val name: String
    val path: String

    data class Folder(
        override val name: String,
        override val path: String
    ) : FileType

    data class Track(
        override val name: String,
        override val path: String
    ) : FileType

    fun toFile() = File(path)

}