package dev.olog.presentation.folder.tree

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.lib.media.mediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.interfaces.CanHandleOnBackPressed
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.widgets.BreadCrumbLayout
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.dimen
import dev.olog.shared.android.extensions.launchIn
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_folder_tree.*
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class FolderTreeFragment : BaseFragment(),
    BreadCrumbLayout.SelectionCallback,
    CanHandleOnBackPressed {

    companion object {

        fun newInstance(): FolderTreeFragment {
            return FolderTreeFragment()
        }
    }

    @Inject
    lateinit var navigator: Navigator

    private val viewModel by viewModels<FolderTreeFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = FolderTreeFragmentAdapter(
            viewModel = viewModel,
            mediaProvider = requireActivity().mediaProvider,
            navigator = navigator
        )
        fab.shrink()

        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.setHasFixedSize(true)

        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

        viewModel.observeCurrentDirectoryFileName()
            .onEach { bread_crumbs.setActiveOrAdd(BreadCrumbLayout.Crumb(it), false) }
            .launchIn(this)

        viewModel.observeChildren()
            .onEach(adapter::submitList)
            .launchIn(this)

        viewModel.observeCurrentFolderIsDefaultFolder()
            .onEach { isDefaultFolder ->
                if (isDefaultFolder){
                    fab.hide()
                } else {
                    fab.show()
                }
            }.launchIn(this)
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

        private val toolbarHeight by lazyFast { dimen(R.dimen.toolbar) }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val currentTranlationY = crumbsWrapper.translationY
            val clampedTranslation = (currentTranlationY - dy).coerceIn(-toolbarHeight.toFloat(), 0f)
            crumbsWrapper.translationY = clampedTranslation
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_folder_tree
}