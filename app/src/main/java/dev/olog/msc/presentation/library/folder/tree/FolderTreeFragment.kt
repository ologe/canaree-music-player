package dev.olog.msc.presentation.library.folder.tree

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.presentation.widget.BreadCrumbLayout
import dev.olog.msc.utils.k.extension.ctx
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.toggleVisibility
import dev.olog.msc.utils.k.extension.windowBackground
import kotlinx.android.synthetic.main.fragment_folder_tree.*
import kotlinx.android.synthetic.main.fragment_folder_tree.view.*
import javax.inject.Inject

class FolderTreeFragment : BaseFragment(), BreadCrumbLayout.SelectionCallback {

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

        viewModel.observeIsExternal()
                .subscribe(this, {
                    fab.toggleVisibility(it.isEnabled, true)
                    fab.setBackgroundResource(if (it.isExternal) R.drawable.vd_sdcard else R.drawable.vd_internal_storage)
                })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.adapter = adapter
        view.list.layoutManager = LinearLayoutManager(context)
        view.list.setHasFixedSize(true)

        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)

        if (AppTheme.isDarkTheme()){
            view.bread_crumbs.setBackgroundColor(ctx.windowBackground())
        }
        if (AppTheme.isGrayMode()){
            view.bread_crumbs.setBackgroundColor(ContextCompat.getColor(ctx, R.color.toolbar))
        }
    }

    override fun onResume() {
        super.onResume()
        bread_crumbs.setCallback(this)
        fab.setOnClickListener { viewModel.toggleIsExternal() }

    }

    override fun onPause() {
        super.onPause()
        bread_crumbs.setCallback(null)
        fab.setOnClickListener(null)
    }

    override fun onCrumbSelection(crumb: BreadCrumbLayout.Crumb, index: Int) {
        viewModel.nextFolder(crumb.file)
    }

    fun pop(): Boolean{
        return viewModel.popFolder()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_folder_tree
}