package dev.olog.presentation.folder.tree

import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaId

@Stable
sealed interface FolderTreeFragmentItem {

    @Stable
    object Back : FolderTreeFragmentItem

    @Stable
    data class Directory(
        val mediaId: MediaId,
        val title: String,
        val path: String,
    ) : FolderTreeFragmentItem

    @Stable
    data class Track(
        val mediaId: MediaId,
        val title: String,
        val path: String,
    ) : FolderTreeFragmentItem

    @Stable
    data class Header(val text: String) : FolderTreeFragmentItem

    companion object : DiffUtil.ItemCallback<FolderTreeFragmentItem>() {
        override fun areItemsTheSame(
            oldItem: FolderTreeFragmentItem,
            newItem: FolderTreeFragmentItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FolderTreeFragmentItem,
            newItem: FolderTreeFragmentItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}