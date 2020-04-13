package dev.olog.feature.library.folder.tree

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.library.R
import dev.olog.lib.media.MediaProvider
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.CanHandleOnBackPressed
import dev.olog.navigation.Navigator
import dev.olog.feature.library.widgets.BreadCrumbLayout
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.feature.presentation.base.extensions.dimen
import dev.olog.shared.clamp
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_folder_tree.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class FolderTreeFragment : BaseFragment(),
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
        list.layoutManager = OverScrollLinearLayoutManager(requireContext())
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