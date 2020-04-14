package dev.olog.feature.library.folder.normal

import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import androidx.core.view.doOnLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.olog.feature.library.R
import dev.olog.feature.library.layout.manager.DefaultSpanSizeLookup
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.extensions.awaitAnimationEnd
import dev.olog.navigation.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_folders_normal.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class FoldersNormalFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<FoldersNormalFragmentViewModel> {
        viewModelFactory
    }

    @Inject
    lateinit var navigator: Navigator

    private val adapter by lazyFast {
        FoldersNormalFragmentAdapter(navigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spanSizeLookup = DefaultSpanSizeLookup(viewModel.getSpanCount())
        val layoutManager = OverScrollGridLayoutManager(requireContext(), spanSizeLookup.getSpanCount())
        layoutManager.spanSizeLookup = spanSizeLookup
        list.layoutManager = layoutManager
        list.adapter = adapter

        viewModel.data
            .onEach {
                // TODO sidebar
                // TODO empty state
                adapter.submitList(it)
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

    override fun provideLayoutId(): Int = R.layout.fragment_folders_normal
}