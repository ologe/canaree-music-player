package dev.olog.presentation.folder.tree

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.databinding.FragmentFolderTreeBinding
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.widgets.BreadCrumbLayout
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.ctx
import dev.olog.shared.android.extensions.dimen
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.viewBinding
import dev.olog.shared.clamp
import dev.olog.shared.lazyFast
import javax.inject.Inject
import dev.olog.presentation.interfaces.CanHandleOnBackPressed

@AndroidEntryPoint
class FolderTreeFragment : Fragment(R.layout.fragment_folder_tree),
    BreadCrumbLayout.SelectionCallback,
    CanHandleOnBackPressed {

    companion object {

        @JvmStatic
        fun newInstance(): FolderTreeFragment {
            return FolderTreeFragment()
        }
    }

    @Inject
    lateinit var navigator: Navigator
    private val viewModel by viewModels<FolderTreeFragmentViewModel>()

    private val binding by viewBinding(FragmentFolderTreeBinding::bind) {
        it.list.adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = FolderTreeFragmentAdapter(
            viewModel,
            requireActivity().findInContext<MediaProvider>(),
            navigator
        )
        binding.fab.shrink()

        binding.list.adapter = adapter
        binding.list.layoutManager = OverScrollLinearLayoutManager(binding.list)
        binding.list.setHasFixedSize(true)

        binding.fastScroller.attachRecyclerView(binding.list)
        binding.fastScroller.showBubble(false)

        viewModel.observeCurrentDirectoryFileName()
            .subscribe(viewLifecycleOwner) {
                binding.breadCrumbs.setActiveOrAdd(BreadCrumbLayout.Crumb(it), false)
            }

        viewModel.observeChildren()
            .subscribe(viewLifecycleOwner, adapter::submitList)

        viewModel.observeCurrentFolderIsDefaultFolder()
            .subscribe(viewLifecycleOwner) { isDefaultFolder ->
                if (isDefaultFolder){
                    binding.fab.hide()
                } else {
                    binding.fab.show()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        binding.breadCrumbs.setCallback(this)
        binding.list.addOnScrollListener(scrollListener)
        binding.fab.setOnClickListener { onFabClick() }
    }

    override fun onPause() {
        super.onPause()
        binding.breadCrumbs.setCallback(null)
        binding.list.removeOnScrollListener(scrollListener)
        binding.fab.setOnClickListener(null)
    }

    private fun onFabClick(){
        if (!binding.fab.isExtended){
            binding.fab.extend()
            return
        }
        viewModel.updateDefaultFolder()
    }

    override fun onCrumbSelection(crumb: BreadCrumbLayout.Crumb, index: Int) {
        viewModel.nextFolder(crumb.file.absoluteFile)
    }

    override fun handleOnBackPressed(): Boolean {
        if (viewModel.canPop) {
            viewModel.popFolder()
            return true
        }
        return false
    }

    private val scrollListener = object : RecyclerView.OnScrollListener(){

        private val toolbarHeight by lazyFast { ctx.dimen(R.dimen.toolbar) }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val currentTranlationY = binding.crumbsWrapper.translationY
            val clampedTranslation = clamp(currentTranlationY - dy, -toolbarHeight.toFloat(), 0f)
            binding.crumbsWrapper.translationY = clampedTranslation
        }
    }

}