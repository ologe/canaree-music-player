package dev.olog.feature.library.folder.tree

import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.library.R
import java.io.File

sealed class FolderTreeFragmentModel {

    abstract val layoutType: Int

    //    title = "...",
    object Back : FolderTreeFragmentModel() {
        override val layoutType: Int = R.layout.item_folder_tree_directory
    }

    data class Header(
        val title: String
    ) : FolderTreeFragmentModel() {
        override val layoutType: Int = R.layout.item_folder_tree_header
    }

    data class Track(
        val mediaId: MediaId,
        val title: String,
        val path: String
    ) : FolderTreeFragmentModel() {
        override val layoutType: Int = R.layout.item_folder_tree_track
    }

    data class Directory(
        val mediaId: MediaId,
        val title: String,
        val path: String,
    ) : FolderTreeFragmentModel() {

        override val layoutType: Int = R.layout.item_folder_tree_directory

        val file: File
            get() = File(path)

    }

}