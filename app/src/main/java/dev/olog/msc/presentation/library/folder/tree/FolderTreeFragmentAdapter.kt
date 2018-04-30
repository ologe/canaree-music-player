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

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType){
            R.layout.item_folder_tree_directory,
            R.layout.item_folder_tree_track -> {
                viewHolder.setOnClickListener(controller) { item, _, _ ->
                    val file = File(item.image)
                    if (item.mediaId == FolderTreeFragmentViewModel.BACK_HEADER_ID || file.isDirectory){
                        viewModel.nextFolder(item)
                    } else {
                        mediaProvider.playFolderTree(file)
                    }
                }
            }
        }

    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }
}