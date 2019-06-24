package dev.olog.msc.presentation.library.folder.tree

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.media.MediaProvider
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.extensions.viewModelProvider
import dev.olog.msc.presentation.widget.BreadCrumbLayout
import dev.olog.msc.utils.k.extension.*
import dev.olog.shared.extensions.asLiveData
import dev.olog.shared.extensions.subscribe
import dev.olog.shared.extensions.toggleVisibility
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

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var navigator: Navigator
    private val viewModel by lazyFast {
        viewModelProvider<FolderTreeFragmentViewModel>(
            viewModelFactory
        )
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        val adapter = FolderTreeFragmentAdapter(lifecycle, viewModel, activity as MediaProvider, navigator)
        view.list.adapter = adapter
        view.list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        view.list.setHasFixedSize(true)

        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)

//        if (AppTheme.isDarkTheme()){ TODO
//            view.bread_crumbs.setBackgroundColor(ctx.windowBackground())
//        }
//        if (AppTheme.isGrayMode()){
//            view.bread_crumbs.setBackgroundColor(ContextCompat.getColor(ctx, R.color.toolbar))
//        }

        viewModel.observeFileName()
                .subscribe(viewLifecycleOwner) {
                    bread_crumbs.setActiveOrAdd(BreadCrumbLayout.Crumb(it), false)
                }

        viewModel.observeChildrens()
                .subscribe(viewLifecycleOwner, adapter::updateDataSet)

        viewModel.observeCurrentFolder()
                .asLiveData()
                .subscribe(viewLifecycleOwner) { isInDefaultFolder ->
                    defaultFolder.toggleVisibility(!isInDefaultFolder, true)
                }
    }

    override fun onResume() {
        super.onResume()
        bread_crumbs.setCallback(this)
        defaultFolder.setOnClickListener { viewModel.updateDefaultFolder() }
    }

    override fun onPause() {
        super.onPause()
        bread_crumbs.setCallback(null)
        defaultFolder.setOnClickListener(null)
    }

    override fun onCrumbSelection(crumb: BreadCrumbLayout.Crumb, index: Int) {
        viewModel.nextFolder(crumb.file.safeGetCanonicalFile())
    }

    fun pop(): Boolean{
        return viewModel.popFolder()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_folder_tree
}