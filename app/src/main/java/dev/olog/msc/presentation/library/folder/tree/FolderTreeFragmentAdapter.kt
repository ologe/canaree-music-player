package dev.olog.msc.presentation.library.folder.tree

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.k.extension.setOnClickListener
import java.io.File
import javax.inject.Inject

@PerFragment
class FolderTreeFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val viewModel: FolderTreeFragmentViewModel,
        private val mediaProvider: MediaProvider

) : AbsAdapter<DisplayableFile>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType){
            R.layout.item_folder_tree_directory,
            R.layout.item_folder_tree_track -> {
                viewHolder.setOnClickListener(controller) { item, _, _ ->
                    when {
                        item.mediaId == FolderTreeFragmentViewModel.BACK_HEADER_ID -> {
                            viewModel.goBack()
                        }
                        item.isFile() && item.asFile().isDirectory -> viewModel.nextFolder(item.asFile())
                        else -> mediaProvider.playFolderTree(item.asFile())
                    }
                }
            }
        }

    }

    override fun bind(binding: ViewDataBinding, item: DisplayableFile, position: Int) {
        binding.setVariable(BR.item, item)
    }
}