package dev.olog.msc.presentation.library.folder.tree

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v4.app.FragmentActivity
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.utils.k.extension.*

class FolderTreeFragmentAdapter (
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
                viewHolder.setOnLongClickListener(controller) { item, _, _ ->
                    if (item.mediaId == FolderTreeFragmentViewModel.BACK_HEADER_ID){
                        return@setOnLongClickListener
                    }
                    var file = item.asFile()
                    if (!file.isDirectory){
                        file = file.parentFile
                    }
                    val context = viewHolder.itemView.context
                    (context as FragmentActivity).simpleDialog {
                        setTitle(R.string.folder_set_default_title)
                        setMessage(context.getString(R.string.folder_set_default_message, file.name).asHtml())
                        setPositiveButton(R.string.popup_positive_ok) { _,_ ->
                            viewModel.updateDefaultFolder(file)
                        }
                        setNegativeButton(R.string.popup_negative_cancel, null)
                    }
                }
            }
        }

    }

    override fun bind(binding: ViewDataBinding, item: DisplayableFile, position: Int) {
        binding.setVariable(BR.item, item)
    }
}