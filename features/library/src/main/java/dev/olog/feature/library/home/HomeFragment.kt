package dev.olog.feature.library.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.SetupNestedList
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.activity.HasBottomNavigation
import dev.olog.feature.presentation.base.adapter.ObservableAdapter
import dev.olog.navigation.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class HomeFragment : BaseFragment(), SetupNestedList {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<HomeFragmentViewModel> {
        viewModelFactory
    }
    private val adapter by lazyFast {
        HomeFragmentAdapter(navigator, requireActivity() as HasBottomNavigation, this)
    }
    private lateinit var layoutManager: LinearLayoutManager

    private val recentlyAddedAdapter by lazyFast {
        HomeFragmentNestedAdapter(navigator)
    }

    private val lastPlayedAdapter by lazyFast {
        HomeFragmentNestedAdapter(navigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = OverScrollLinearLayoutManager(requireContext())
        list.layoutManager = layoutManager
        list.adapter = adapter

        viewModel.data
            .onEach { adapter.submitList(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.recentlyAdded
            .onEach { recentlyAddedAdapter.submitList(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.lastPlayed
            .onEach { lastPlayedAdapter.submitList(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun setupNestedList(layoutId: Int, recyclerView: RecyclerView) {
        when (layoutId) {
            R.layout.item_tab_last_played_album_horizontal_list -> setupHorizontalList(
                recyclerView, lastPlayedAdapter
            )
            R.layout.item_tab_new_album_horizontal_list -> setupHorizontalList(
                recyclerView, recentlyAddedAdapter
            )
        }
    }

    private fun setupHorizontalList(list: RecyclerView, adapter: ObservableAdapter<*>) {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
    }

    override fun provideLayoutId(): Int = R.layout.fragment_home
}