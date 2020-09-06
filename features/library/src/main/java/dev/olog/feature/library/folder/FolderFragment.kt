package dev.olog.feature.library.folder

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.extensions.dimen
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_folder.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class FolderFragment : BaseFragment() {

    private val viewModel by viewModels<FolderFragmentViewModel>()

//    private val adapter by lazyFast {
//        FolderFragmentAdapter(viewModel)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val layoutManager = OverScrollGridLayoutManager(list, LibrarySpanSizeLookup.SPAN_COUNT)
//        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                 TODO this is layout position, not adapter position
//                val item = adapter.getItem(position)
//                if (item is FolderFragmentItem.Header || item is FolderFragmentItem.Header) {
//                    return LibrarySpanSizeLookup.SPAN_COUNT
//                }
//                if (viewModel.isHierarchyEnabled) {
//                    return LibrarySpanSizeLookup.SPAN_COUNT
//                }
//                return LibrarySpanSizeLookup.SPAN_COUNT / viewModel.getSpanCount()
//            }
//        }
//
//        list.adapter = adapter
//        list.layoutManager = layoutManager

        viewModel.isHierarchyFlow
            .onEach {  isHierarchy ->
                hierarchy.isSelected = isHierarchy
                sidebar.isVisible = !isHierarchy // TODO animate visibility?
                val margin = if (isHierarchy) R.dimen.default_list_margin_horizontal else R.dimen.tab_margin_end
                list.updatePadding(right = requireContext().dimen(margin))
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.data
            .onEach {
//                adapter.submitList(it)
                // TODO empty state
                // TODO sidebar
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        hierarchy.setOnClickListener {
            viewModel.setIsHierarchy(!hierarchy.isSelected)
        }
    }

    override fun onPause() {
        super.onPause()
        hierarchy.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_folder
}