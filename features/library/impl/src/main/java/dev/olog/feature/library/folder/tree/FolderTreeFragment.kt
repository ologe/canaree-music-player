package dev.olog.feature.library.folder.tree

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.base.BaseFragment
import dev.olog.feature.base.CanHandleOnBackPressed
import dev.olog.feature.base.adapter.TextHeaderAdapter
import dev.olog.feature.dialogs.FeatureDialogsNavigator
import dev.olog.feature.library.R
import dev.olog.feature.library.folder.tree.adapter.FolderTreeFragmentAdapter
import dev.olog.feature.library.folder.tree.adapter.FolderTreeFragmentBackAdapter
import dev.olog.feature.library.folder.tree.adapter.FolderTreeFragmentItemAdapter
import dev.olog.feature.library.widget.BreadCrumbLayout
import dev.olog.media.mediaProvider
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.collectOnLifecycle
import dev.olog.shared.android.extensions.ctx
import dev.olog.shared.android.extensions.dimen
import dev.olog.shared.android.extensions.subscribe
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
    lateinit var dialogNavigator: FeatureDialogsNavigator

    private val viewModel by viewModels<FolderTreeFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = FolderTreeFragmentAdapter(
            FolderTreeFragmentBackAdapter { viewModel.popFolder() },
            TextHeaderAdapter(getString(localization.R.string.common_folders)),
            FolderTreeFragmentItemAdapter(
                onItemClick = { viewModel.nextFolder(it.toFile()) },
                onItemLongClick = { _, _ -> },
            ),
            TextHeaderAdapter(getString(localization.R.string.common_tracks)),
            FolderTreeFragmentItemAdapter(
                onItemClick = {
                    val uri = viewModel.createMediaId(it) ?: return@FolderTreeFragmentItemAdapter
                    mediaProvider.playFromMediaId(uri)
                },
                onItemLongClick = { item, v ->
                    dialogNavigator.toDialog(
                        activity = requireActivity(),
                        uri = viewModel.createMediaId(item) ?: return@FolderTreeFragmentItemAdapter,
                        anchor = v
                    )
                },
            ),
        )
        fab.shrink()

        list.adapter = adapter
        list.layoutManager = OverScrollLinearLayoutManager(list)
        list.setHasFixedSize(true)

        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

        viewModel.observeCurrentDirectoryFileName()
            .collectOnLifecycle(this) {
                bread_crumbs.setActiveOrAdd(BreadCrumbLayout.Crumb(it), false)
                adapter.submitCurrentFile(it)
            }

        viewModel.data()
            .collectOnLifecycle(this) { (folders, tracks) ->
                adapter.submit(folders, tracks)
        }

        viewModel.observeCurrentFolderIsDefaultFolder()
            .collectOnLifecycle(this) { isDefaultFolder ->
                if (isDefaultFolder){
                    fab.hide()
                } else {
                    fab.show()
                }
            }

        fab.setOnClickListener { onFabClick() }
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

        private val toolbarHeight by lazyFast { ctx.dimen(dev.olog.shared.android.R.dimen.toolbar) }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val currentTranlationY = crumbsWrapper.translationY
            val clampedTranslation = clamp(currentTranlationY - dy, -toolbarHeight.toFloat(), 0f)
            crumbsWrapper.translationY = clampedTranslation
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_folder_tree
}