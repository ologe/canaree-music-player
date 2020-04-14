package dev.olog.feature.library.album

import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import androidx.core.view.doOnLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.olog.feature.library.R
import dev.olog.feature.library.layout.manager.BaseSpanSizeLookup
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.extensions.awaitAnimationEnd
import dev.olog.navigation.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_albums.*
import kotlinx.android.synthetic.main.fragment_albums.list
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class AlbumsFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<AlbumsFragmentViewModel> {
        viewModelFactory
    }

    @Inject
    lateinit var navigator: Navigator

    private val adapter by lazyFast {
        AlbumsFragmentAdapter(navigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spanSizeLookup = BaseSpanSizeLookup(viewModel.getSpanCount())
        val layoutManager = OverScrollGridLayoutManager(requireContext(), spanSizeLookup.getSpanCount())
        layoutManager.spanSizeLookup = spanSizeLookup

        list.layoutManager = layoutManager
        list.adapter = adapter

        viewModel.data
            .onEach {
                adapter.submitList(it)
                // TODO side bar
                // TODo empty text
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.observeSpanCount()
            .onEach { span ->
                list.awaitAnimationEnd()
                list.doOnLayout {
                    // TODO check and improve
                    TransitionManager.beginDelayedTransition(list)
                    spanSizeLookup.requestedSpanSize = span
                    adapter.notifyDataSetChanged()
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_albums
}