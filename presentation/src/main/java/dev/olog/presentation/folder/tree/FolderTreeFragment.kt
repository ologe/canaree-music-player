package dev.olog.presentation.folder.tree

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.media.api.mediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.interfaces.CanHandleOnBackPressed
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.widgets.BreadCrumbLayout
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.platform.extension.ctx
import dev.olog.platform.extension.dimen
import dev.olog.shared.subscribe
import dev.olog.shared.clamp
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_folder_tree.*
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
            lifecycle,
            viewModel,
            mediaProvider,
            navigator
        )

        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.setHasFixedSize(true)

        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

        viewModel.observeCurrentDirectoryFileName()
            .subscribe(viewLifecycleOwner) {
                bread_crumbs.setActiveOrAdd(BreadCrumbLayout.Crumb(it))
            }

        viewModel.observeChildren()
            .subscribe(viewLifecycleOwner) {
                adapter.updateDataSet(it)
                // TODO scroll to top on change
            }
    }

    override fun onResume() {
        super.onResume()
        bread_crumbs.setCallback(this)
        list.addOnScrollListener(scrollListener)
    }

    override fun onPause() {
        super.onPause()
        bread_crumbs.setCallback(null)
        list.removeOnScrollListener(scrollListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
    }

    override fun onCrumbSelection(crumb: BreadCrumbLayout.Crumb, index: Int) {
        viewModel.nextFolder(crumb.file.path)
    }

    override fun handleOnBackPressed(): Boolean {
        return viewModel.popFolder()
    }

    private val scrollListener = object : RecyclerView.OnScrollListener(){

        private val toolbarHeight by lazyFast { ctx.dimen(R.dimen.toolbar) }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val currentTranlationY = crumbsWrapper.translationY
            val clampedTranslation = clamp(currentTranlationY - dy, -toolbarHeight.toFloat(), 0f)
            crumbsWrapper.translationY = clampedTranslation
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_folder_tree
}