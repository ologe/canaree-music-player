package dev.olog.feature.library.folder

import dev.olog.feature.presentation.base.model.PresentationId
import javax.annotation.concurrent.Immutable

@Immutable
sealed class FolderFragmentModel {

    @Immutable
    data class Header(
        val title: String
    ) : FolderFragmentModel()

    @Immutable
    data class Album(
        val mediaId: PresentationId.Category,
        val title: String,
        val subtitle: String
    ) : FolderFragmentModel()

    @Immutable
    data class File(
        val mediaId: PresentationId.Category, // don't have the id, just the path
        val title: String,
        val path: String
    ) : FolderFragmentModel()

    @Immutable
    data class Folder(
        val mediaId: PresentationId.Category,
        val title: String,
        val path: String
    ) : FolderFragmentModel() {

        val file: java.io.File
            get() = java.io.File(path)

    }

}