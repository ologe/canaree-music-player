package dev.olog.feature.library.folder.tree.adapter

import android.os.Environment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.folder.FileType
import dev.olog.feature.base.adapter.CustomConcatAdapter
import dev.olog.feature.base.adapter.firstByType
import dev.olog.feature.base.adapter.requireHeaderOf
import java.io.File

class FolderTreeFragmentAdapter(
    vararg adapters: RecyclerView.Adapter<*>
) : CustomConcatAdapter(
    ConcatAdapter.Config.DEFAULT,
    adapters.toList()
) {

    fun submit(folders: List<FileType.Folder>, tracks: List<FileType.Track>) {
        val (foldersAdapters, tracksAdapters) = delegate.adapters
            .filterIsInstance<FolderTreeFragmentItemAdapter>()
            .take(2)

        foldersAdapters.submitList(folders)
        delegate.requireHeaderOf(foldersAdapters).show = folders.isNotEmpty()

        tracksAdapters.submitList(tracks)
        delegate.requireHeaderOf(tracksAdapters).show = tracks.isNotEmpty()
    }

    fun submitCurrentFile(file: File) {
        val adapter = delegate.firstByType<FolderTreeFragmentBackAdapter>()
        adapter.show = file != Environment.getRootDirectory()
    }

}