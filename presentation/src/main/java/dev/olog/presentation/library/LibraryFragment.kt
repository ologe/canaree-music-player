package dev.olog.presentation.library

import android.os.Bundle
import android.view.View
import android.widget.TextView
import dev.olog.analytics.TrackerFacade
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.viewLifecycleScope
import dev.olog.presentation.databinding.FragmentLibraryBinding
import dev.olog.presentation.interfaces.HasBottomNavigation
import dev.olog.presentation.model.BottomNavigationPage
import dev.olog.presentation.model.LibraryPage
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.viewBinding
import dev.olog.shared.lazyFast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class LibraryFragment : BaseFragment(R.layout.fragment_library) {

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

    @Inject
    internal lateinit var presenter: LibraryFragmentPresenter
    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var trackerFacade: TrackerFacade

    private val isPodcast by lazyFast {
        getArgument<Boolean>(
            IS_PODCAST
        )
    }

    private val pagerAdapter by lazyFast {
        LibraryFragmentAdapter(
            act.applicationContext, childFragmentManager, presenter.getCategories(isPodcast)
        )
    }

    fun isCurrentFragmentFolderTree(): Boolean {
        return pagerAdapter.getCategoryAtPosition(binding.viewPager.currentItem) == MediaIdCategory.FOLDERS &&
                pagerAdapter.showFolderAsHierarchy()
    }

    private val binding by viewBinding(FragmentLibraryBinding::bind)

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
        binding.viewPager.currentItem = presenter.getViewPagerLastPage(pagerAdapter.count, isPodcast)
        binding.viewPager.offscreenPageLimit = 5

        binding.pagerEmptyState.toggleVisibility(pagerAdapter.isEmpty(), true)

        val selectedView: TextView = if (!isPodcast) binding.tracks else binding.podcasts
        val unselectedView: TextView = if (!isPodcast) binding.podcasts else binding.tracks
        selectedView.setTextColor(requireContext().textColorPrimary())
        unselectedView.setTextColor(requireContext().textColorSecondary())

        if (!presenter.canShowPodcasts()){
            binding.podcasts.setGone()
        }

        if (presenter.showFloatingWindowTutorialIfNeverShown()) {
            viewLifecycleScope.launch {
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.adapter = null
    }

    private fun changeLibraryPage(page: LibraryPage) {
        presenter.setLibraryPage(page)
        (requireActivity() as HasBottomNavigation).navigate(BottomNavigationPage.LIBRARY)
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
                presenter.setViewPagerLastPage(position, isPodcast)
                pagerAdapter.getCategoryAtPosition(position)?.let {
                    trackerFacade.trackScreen(it.toString(), null)
                }

            }
        }

}