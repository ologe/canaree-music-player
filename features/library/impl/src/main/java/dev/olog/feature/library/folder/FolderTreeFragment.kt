package dev.olog.feature.library.folder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.library.R
import dev.olog.feature.main.api.FeatureMainPopupNavigator
import dev.olog.platform.CanHandleOnBackPressed
import dev.olog.platform.fragment.BaseFragment
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.extension.collectOnViewLifecycle
import dev.olog.shared.extension.dimen
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.lazyFast
import dev.olog.shared.extension.subscribe
import dev.olog.ui.BreadCrumbLayout
import kotlinx.android.synthetic.main.fragment_folder_tree.*
import javax.inject.Inject

@AndroidEntryPoint
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
    lateinit var featureMainPopupNavigator: FeatureMainPopupNavigator

    private val viewModel by viewModels<FolderTreeFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = FolderTreeFragmentAdapter(
            viewModel = viewModel,
            mediaProvider = requireContext().findInContext(),
            onItemLongClick = { v, mediaId ->
                featureMainPopupNavigator.toItemDialog(v, mediaId)
            }
        )
        fab.shrink()

        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.setHasFixedSize(true)

        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

        viewModel.observeCurrentDirectoryFileName()
            .collectOnViewLifecycle(this) {
                bread_crumbs.setActiveOrAdd(BreadCrumbLayout.Crumb(it), false)
            }

        viewModel.observeChildren()
            .subscribe(viewLifecycleOwner, adapter::submitList)

        viewModel.observeCurrentFolderIsDefaultFolder()
            .subscribe(viewLifecycleOwner) { isDefaultFolder ->
                if (isDefaultFolder){
                    fab.hide()
                } else {
                    fab.show()
                }
            }
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

        private val toolbarHeight by lazyFast { dimen(dev.olog.ui.R.dimen.toolbar) }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val currentTranlationY = crumbsWrapper.translationY
            val clampedTranslation = (currentTranlationY - dy).coerceIn(-toolbarHeight.toFloat(), 0f)
            crumbsWrapper.translationY = clampedTranslation
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_folder_tree
}