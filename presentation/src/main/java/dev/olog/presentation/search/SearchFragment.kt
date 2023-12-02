package dev.olog.presentation.search

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.R
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.restoreUpperWidgetsTranslation
import dev.olog.presentation.databinding.FragmentSearchBinding
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.search.adapter.SearchFragmentAdapter
import dev.olog.presentation.utils.hideIme
import dev.olog.presentation.utils.showIme
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search),
    IDragListener by DragListenerImpl() {

    companion object {
        val TAG = SearchFragment::class.java.name

        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    private val binding by viewBinding(FragmentSearchBinding::bind)
    private val viewModel by viewModels<SearchFragmentViewModel>()

    private val adapter by lazyFast {
        SearchFragmentAdapter(
            mediaProvider = requireActivity().findInContext(),
            navigator = navigator,
            viewModel = viewModel
        )
    }

    @Inject
    lateinit var navigator: Navigator
    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutManager = OverScrollLinearLayoutManager(binding.list)
        binding.list.adapter = adapter
        binding.list.layoutManager = layoutManager
        binding.list.setHasFixedSize(true)

        setupDragListener(binding.list, ItemTouchHelper.LEFT)

        viewModel.observeData()
            .subscribe(viewLifecycleOwner) {
                adapter.submitList(it)
                binding.emptyStateText.toggleVisibility(it.isEmpty(), true)
                restoreUpperWidgetsTranslation()
            }

        viewLifecycleScope.launch {
            binding.editText.afterTextChange()
                .debounce(200)
                .filter { it.isBlank() || it.trim().length >= 2 }
                .collect { viewModel.updateQuery(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        binding.fab.setOnClickListener { binding.editText.showIme() }

        binding.floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
        binding.more.setOnClickListener { navigator.toMainPopup(it, null) }
    }

    override fun onPause() {
        super.onPause()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
        binding.fab.setOnClickListener(null)
        binding.floatingWindow.setOnClickListener(null)
        binding.more.setOnClickListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.list.adapter = null
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(requireActivity())
    }


    override fun onStop() {
        super.onStop()
        binding.editText.hideIme()
    }

}