package dev.olog.msc.presentation.library.folder.tree

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.widget.BreadCrumbLayout
import dev.olog.msc.utils.k.extension.subscribe
import kotlinx.android.synthetic.main.fragment_folder_tree.*
import kotlinx.android.synthetic.main.fragment_folder_tree.view.*
import javax.inject.Inject

class FolderTreeFragment : BaseFragment() {

    companion object {

        @JvmStatic
        fun newInstance(): FolderTreeFragment {
            return FolderTreeFragment()
        }
    }

    @Inject lateinit var adapter: FolderTreeFragmentAdapter
    @Inject lateinit var viewModel: FolderTreeFragmentViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.observeFileName()
                .subscribe(this, {
                    bread_crumbs.setActiveOrAdd(BreadCrumbLayout.Crumb(it), false)
                })

        viewModel.observeChildrens()
                .subscribe(this, adapter::updateDataSet)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.adapter = adapter
        view.list.layoutManager = LinearLayoutManager(context)
        view.list.setHasFixedSize(true)

        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_folder_tree
}