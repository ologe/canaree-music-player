package dev.olog.presentation.folder.tree

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.interfaces.CanHandleOnBackPressed
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.widgets.BreadCrumbLayout
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.dimen
import dev.olog.shared.clamp
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_folder_tree.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    internal lateinit var navigator: Navigator
    private val viewModel by activityViewModels<FolderTreeFragmentViewModel> {
        viewModelFactory
    }

    private val adapter by lazyFast {
        FolderTreeFragmentAdapter(viewModel, activity as MediaProvider, navigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.breadCrumbState?.let {
            bread_crumbs.restoreFromStateWrapper(it)
        }

        fab.shrink()

        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.setHasFixedSize(true)

        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

        viewModel.currentDirectoryFileName
            .onEach { bread_crumbs.setActiveOrAdd(BreadCrumbLayout.Crumb(it), false) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.children
            .onEach {
                restoreUpperWidgetsTranslation()
                crumbsWrapper.animate().translationY(0f)
                adapter.submitList(it)
                emptyState.isVisible = it.isEmpty()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.canSaveDefaultFolder
            .onEach {
                if (it && !fab.isVisible) {
                    fab.show()
                } else if (!it && fab.isVisible) {
                    fab.hide()
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        bread_crumbs.setCallback(this)
        list.addOnScrollListener(scrollListener)
        fab.setOnClickListener { onFabClick() }
    }

    override fun onPause() {
        super.onPause()
        bread_crumbs.setCallback(null)
        list.removeOnScrollListener(scrollListener)
        fab.setOnClickListener(null)
        viewModel.breadCrumbState = bread_crumbs.stateWrapper
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
    }

    private fun onFabClick(){
        if (!fab.isExtended){
            fab.extend()
            return
        }
        viewModel.updateDefaultFolder()
    }

    override fun onCrumbSelection(crumb: BreadCrumbLayout.Crumb, index: Int) {
        viewModel.nextFolder(crumb.file.absoluteFile)
    }

    override fun handleOnBackPressed(): Boolean {
        return viewModel.popFolder()
    }

    private val scrollListener = object : RecyclerView.OnScrollListener(){

        private val toolbarHeight by lazyFast { requireContext().dimen(R.dimen.toolbar) }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val currentTranlationY = crumbsWrapper.translationY
            val clampedTranslation = clamp(
                currentTranlationY - dy,
                -toolbarHeight.toFloat(),
                0f
            )
            crumbsWrapper.translationY = clampedTranslation
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_folder_tree
}