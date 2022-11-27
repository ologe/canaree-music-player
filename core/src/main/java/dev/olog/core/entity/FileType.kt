package dev.olog.core.entity

sealed class FileType {

    data class Folder(
        val name: String,
        val path: String
    ) : FileType()

    data class Track(
        val id: Long,
        val title: String,
        val path: String
    ) : FileType()
}