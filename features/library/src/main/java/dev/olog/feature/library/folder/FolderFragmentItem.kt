package dev.olog.feature.library.folder

import dev.olog.feature.presentation.base.model.PresentationId

sealed class FolderFragmentItem {

    data class BreadCrumb(
        val file: java.io.File
    ) : FolderFragmentItem()

    data class Header(
        val title: String
    ) : FolderFragmentItem()

    data class Album(
        val mediaId: PresentationId.Category,
        val title: String,
        val subtitle: String
    ) : FolderFragmentItem()

    data class File(
        val mediaId: PresentationId.Category, // don't have the id, just the path
        val title: String,
        val path: String
    ) : FolderFragmentItem()

    data class Folder(
        val mediaId: PresentationId.Category,
        val title: String,
        val path: String
    ) : FolderFragmentItem()

}