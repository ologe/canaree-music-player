package dev.olog.msc.presentation.library.folder.tree

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.presentation.base.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.base.setOnClickListener
import dev.olog.presentation.base.setOnLongClickListener

class FolderTreeFragmentAdapter(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val viewModel: FolderTreeFragmentViewModel,
        private val mediaProvider: MediaProvider,
        private val navigator: Navigator

) : AbsAdapter<DisplayableFile>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_folder_tree_directory,
            R.layout.item_folder_tree_track -> {
                viewHolder.setOnClickListener(controller) { item, _, _ ->
                    when {
                        item.mediaId == FolderTreeFragmentViewModel.BACK_HEADER_ID -> viewModel.goBack()
                        item.isFile() && item.asFile().isDirectory -> viewModel.nextFolder(item.asFile())
                        else -> {
                            viewModel.createMediaId(item)?.let { mediaId ->
                                mediaProvider.playFromMediaId(mediaId, null)
                            }

                        }
                    }
                }
                viewHolder.setOnLongClickListener(controller) { item, _, view ->
                    if (item.mediaId == FolderTreeFragmentViewModel.BACK_HEADER_ID) {
                        return@setOnLongClickListener
                    }
                    if (!item.asFile().isDirectory) {
                        viewModel.createMediaId(item)?.let { mediaId ->
                            navigator.toDialog(mediaId, view)
                        }
                    }
                }
            }
        }

    }

    override fun bind(binding: ViewDataBinding, item: DisplayableFile, position: Int) {
        binding.setVariable(BR.item, item)
    }
}