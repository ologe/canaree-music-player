package dev.olog.domain.entity

sealed class FileType {

    data class Folder(
        val name: String,
        val path: String
    ) : FileType()

    data class Track(
        val title: String,
        val path: String
    ) : FileType()
}