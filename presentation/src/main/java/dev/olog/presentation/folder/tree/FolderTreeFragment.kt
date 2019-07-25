package dev.olog.presentation.folder.tree

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.interfaces.CanHandleOnBackPressed
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.widgets.BreadCrumbLayout
import dev.olog.shared.android.extensions.*
import kotlinx.android.synthetic.main.fragment_folder_tree.*
import javax.inject.Inject

class FolderTreeFragment : BaseFragment(),
    BreadCrumbLayout.SelectionCallback,
    CanHandleOnBackPressed {

    companion object {

        @JvmStatic
        fun newInstance(): FolderTreeFragment {
            return FolderTreeFragment()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var navigator: Navigator
    private val viewModel by lazyFast {
        viewModelProvider<FolderTreeFragmentViewModel>(
            viewModelFactory
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = FolderTreeFragmentAdapter(
            lifecycle,
            viewModel,
            activity as MediaProvider,
            navigator
        )
        list.adapter = adapter
        list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        list.setHasFixedSize(true)

        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

//        if (AppTheme.isDarkTheme()){ TODO
//            bread_crumbs.setBackgroundColor(ctx.windowBackground())
//        }
//        if (AppTheme.isGrayMode()){
//            bread_crumbs.setBackgroundColor(ContextCompat.getColor(ctx, R.color.toolbar))
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

    override fun handleOnBackPressed(): Boolean {
        return viewModel.popFolder()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_folder_tree
}