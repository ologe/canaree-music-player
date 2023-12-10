package dev.olog.presentation.detail


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.presentation.NavigationUtils
import dev.olog.presentation.R
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.restoreUpperWidgetsTranslation
import dev.olog.presentation.databinding.FragmentDetailBinding
import dev.olog.presentation.detail.adapter.DetailFragmentAdapter
import dev.olog.presentation.interfaces.CanChangeStatusBarColor
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.utils.removeLightStatusBar
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.afterTextChange
import dev.olog.shared.android.extensions.colorControlNormal
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.getArgument
import dev.olog.shared.android.extensions.isDarkMode
import dev.olog.shared.android.extensions.isTablet
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.extensions.viewBinding
import dev.olog.shared.android.extensions.viewLifecycleScope
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.properties.Delegates

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail),
    CanChangeStatusBarColor,
    IDragListener by DragListenerImpl() {

    companion object {
        val TAG = DetailFragment::class.java.name

        fun newInstance(mediaId: MediaId): DetailFragment {
            return DetailFragment().withArguments(
                NavigationUtils.ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject
    lateinit var navigator: Navigator

    private val binding by viewBinding(FragmentDetailBinding::bind) { binding ->
        binding.list.adapter = null
    }
    private val viewModel by viewModels<DetailFragmentViewModel>()

    private val mediaId by lazyFast {
        val mediaId = getArgument<String>(NavigationUtils.ARGUMENTS_MEDIA_ID)
        MediaId.fromString(mediaId)
    }

    private val adapter by lazyFast {
        DetailFragmentAdapter(
            mediaId = mediaId,
            navigator = navigator,
            mediaProvider = act.findInContext(),
            viewModel = viewModel,
            dragListener = this
        )
    }

    private val recyclerOnScrollListener by lazyFast {
        HeaderVisibilityScrollListener(
            this
        )
    }

    internal var hasLightStatusBarColor by Delegates.observable(false) { _, old, new ->
        if (old != new){
            adjustStatusBarColor(new)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.layoutManager = OverScrollLinearLayoutManager(binding.list)
        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)

        var swipeDirections = ItemTouchHelper.LEFT
        if (adapter.canSwipeRight) {
            swipeDirections = swipeDirections or ItemTouchHelper.RIGHT
        }
        setupDragListener(binding.list, swipeDirections)

        binding.fastScroller.attachRecyclerView(binding.list)
        binding.fastScroller.showBubble(false)

        viewModel.observeSongs()
            .subscribe(viewLifecycleOwner) { list ->
                if (list.isEmpty()) {
                    act.onBackPressed()
                } else {
                    adapter.submitList(list)
                    restoreUpperWidgetsTranslation()
                }
            }

        viewLifecycleScope.launch {
            binding.editText.afterTextChange()
                .debounce(200)
                .filter { it.isEmpty() || it.length >= 2 }
                .collect {
                    viewModel.updateFilter(it)
                }
        }
    }


    override fun onResume() {
        super.onResume()
        binding.list.addOnScrollListener(recyclerOnScrollListener)
        binding.list.addOnScrollListener(scrollListener)
        binding.back.setOnClickListener { act.onBackPressed() }
        binding.more.setOnClickListener { navigator.toDialog(viewModel.mediaId, binding.more) }
        binding.filter.setOnClickListener {
            binding.searchWrapper.toggleVisibility(!binding.searchWrapper.isVisible, true)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.list.removeOnScrollListener(recyclerOnScrollListener)
        binding.list.removeOnScrollListener(scrollListener)
        binding.back.setOnClickListener(null)
        binding.more.setOnClickListener(null)
        binding.filter.setOnClickListener(null)
    }

    override fun adjustStatusBarColor() {
        adjustStatusBarColor(hasLightStatusBarColor)
    }

    override fun adjustStatusBarColor(lightStatusBar: Boolean) {
        if (lightStatusBar) {
            setLightStatusBar()
        } else {
            removeLightStatusBar()
        }
    }

    private fun removeLightStatusBar() {
        val color = Color.WHITE
        binding.back.setColorFilter(color)
        binding.more.setColorFilter(color)
        binding.filter.setColorFilter(color)

        if (requireContext().isTablet){
            return
        }
        act.window.removeLightStatusBar()
    }

    private fun setLightStatusBar() {
        if (requireContext().isDarkMode()) {
            return
        }
        val color = requireContext().colorControlNormal()
        binding.back.setColorFilter(color)
        binding.more.setColorFilter(color)
        binding.filter.setColorFilter(color)

        if (requireContext().isTablet){
            return
        }

        act.window.setLightStatusBar()
    }

    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val alpha = 1 - abs(binding.toolbar.translationY) / binding.toolbar.height
            binding.back.alpha = alpha
            binding.filter.alpha = alpha
            binding.more.alpha = alpha
            binding.searchWrapper.alpha = alpha
            binding.headerText.alpha = alpha
        }
    }
}
