package dev.olog.feature.library.folder.tree

import androidx.annotation.LayoutRes
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.library.R
import java.io.File

sealed class FolderTreeFragmentModel(
    @LayoutRes open val layoutType: Int
) {

    //    title = "...",
    object Back : FolderTreeFragmentModel(R.layout.item_folder_tree_directory)

    data class Header(
        val title: String
    ) : FolderTreeFragmentModel(R.layout.item_folder_tree_header)

    data class Track(
        val mediaId: MediaId,
        val title: String,
        val path: String
    ) : FolderTreeFragmentModel(R.layout.item_folder_tree_track)

    data class Directory(
        val mediaId: MediaId,
        val title: String,
        val path: String,
    ) : FolderTreeFragmentModel(R.layout.item_folder_tree_directory) {

        val file: File
            get() = File(path)

    }

}