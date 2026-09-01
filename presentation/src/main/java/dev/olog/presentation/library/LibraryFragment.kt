package dev.olog.presentation.library

import android.os.Bundle
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.databinding.FragmentLibraryBinding
import dev.olog.presentation.interfaces.HasBottomNavigation
import dev.olog.presentation.model.BottomNavigationPage
import dev.olog.presentation.model.LibraryPage
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : BaseFragment() {

    companion object {
        @JvmStatic
        val TAG_TRACK = LibraryFragment::class.java.name
        @JvmStatic
        val TAG_PODCAST = LibraryFragment::class.java.name + ".podcast"
        const val IS_PODCAST = "IS_PODCAST"

        @JvmStatic
        fun newInstance(isPodcast: Boolean): LibraryFragment {
            return LibraryFragment().withArguments(
                IS_PODCAST to isPodcast
            )
        }
    }

    private val viewModel by viewModels<LibraryFragmentViewModel>()
    @Inject
    lateinit var navigator: Navigator

    private val isPodcast by lazyFast {
        getArgument<Boolean>(
            IS_PODCAST
        )
    }

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.adapter = null
        _binding = null
    }

    private val pagerAdapter by lazyFast {
        LibraryFragmentAdapter(
            act.applicationContext, childFragmentManager, viewModel.getCategories(isPodcast)
        )
    }

    fun isCurrentFragmentFolderTree(): Boolean {
        return pagerAdapter.getCategoryAtPosition(binding.viewPager.currentItem) == MediaIdCategory.FOLDERS &&
                pagerAdapter.showFolderAsHierarchy()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null){
            val transaction = childFragmentManager.beginTransaction()
            for (fragment in childFragmentManager.fragments) {
                transaction.remove(fragment)
            }
            transaction.commitNowAllowingStateLoss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.currentItem = viewModel.getViewPagerLastPage(pagerAdapter.count, isPodcast)
        binding.viewPager.offscreenPageLimit = 5

        binding.pagerEmptyState.toggleVisibility(pagerAdapter.isEmpty(), true)

        val selectedView: TextView = if (!isPodcast) binding.tracks else binding.podcasts
        val unselectedView: TextView = if (!isPodcast) binding.podcasts else binding.tracks
        selectedView.setTextColor(requireContext().textColorPrimary())
        unselectedView.setTextColor(requireContext().textColorSecondary())

        if (!viewModel.canShowPodcasts()){
            binding.podcasts.setGone()
        }

        if (viewModel.showFloatingWindowTutorialIfNeverShown()) {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(500)
                TutorialTapTarget.floatingWindow(binding.floatingWindow)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.viewPager.addOnPageChangeListener(onPageChangeListener)
        binding.more.setOnClickListener { navigator.toMainPopup(it, createMediaId()) }
        binding.floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }

        binding.tracks.setOnClickListener { changeLibraryPage(LibraryPage.TRACKS) }
        binding.podcasts.setOnClickListener { changeLibraryPage(LibraryPage.PODCASTS) }
    }

    override fun onPause() {
        super.onPause()
        binding.viewPager.removeOnPageChangeListener(onPageChangeListener)
        binding.more.setOnClickListener(null)
        binding.floatingWindow.setOnClickListener(null)
        binding.tracks.setOnClickListener(null)
        binding.podcasts.setOnClickListener(null)
    }

    private fun changeLibraryPage(page: LibraryPage) {
        viewModel.setLibraryPage(page)
        requireActivity().asType<HasBottomNavigation>().navigate(BottomNavigationPage.LIBRARY)
    }

    private fun createMediaId(): MediaIdCategory? {
        return pagerAdapter.getCategoryAtPosition(binding.viewPager.currentItem)
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!)
    }

    private val onPageChangeListener =
        object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                viewModel.setViewPagerLastPage(position, isPodcast)
            }
        }

}