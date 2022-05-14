package dev.olog.feature.library

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.feature.bubble.api.FeatureBubbleNavigator
import dev.olog.feature.library.api.LibraryPage
import dev.olog.feature.main.api.BottomNavigationPage
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.main.api.HasBottomNavigation
import dev.olog.platform.fragment.BaseFragment
import dev.olog.shared.extension.argument
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.launchWhenResumed
import dev.olog.shared.extension.lazyFast
import dev.olog.shared.extension.withArguments
import dev.olog.ui.textColorPrimary
import dev.olog.ui.textColorSecondary
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.coroutines.delay
import javax.inject.Inject

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
    lateinit var featureMainNavigationPage: FeatureMainNavigator

    @Inject
    lateinit var featureBubbleNavigator: FeatureBubbleNavigator

    private val isPodcast by argument<Boolean>(IS_PODCAST)

    private val pagerAdapter by lazyFast {
        LibraryFragmentAdapter(
            requireContext().applicationContext, childFragmentManager, viewModel.getCategories(isPodcast)
        )
    }

    fun isCurrentFragmentFolderTree(): Boolean {
        return pagerAdapter.getCategoryAtPosition(viewPager.currentItem) == MediaIdCategory.FOLDERS &&
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
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = viewModel.getViewPagerLastPage(pagerAdapter.count, isPodcast)
        viewPager.offscreenPageLimit = 5

        pagerEmptyState.isVisible = pagerAdapter.isEmpty()

        val selectedView: TextView = if (!isPodcast) tracks else podcasts
        val unselectedView: TextView = if (!isPodcast) podcasts else tracks
        selectedView.setTextColor(requireContext().textColorPrimary())
        unselectedView.setTextColor(requireContext().textColorSecondary())

        if (!viewModel.canShowPodcasts()){
            podcasts.isVisible = false
        }

        if (viewModel.showFloatingWindowTutorialIfNeverShown()) {
            launchWhenResumed {
                delay(500)
                LibraryTutorial.floatingWindow(floatingWindow)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewPager.addOnPageChangeListener(onPageChangeListener)
        more.setOnClickListener { featureMainNavigationPage.toMainPopup(requireActivity(), it, createMediaId()) }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }

        tracks.setOnClickListener { changeLibraryPage(LibraryPage.TRACKS) }
        podcasts.setOnClickListener { changeLibraryPage(LibraryPage.PODCASTS) }
    }

    override fun onPause() {
        super.onPause()
        viewPager.removeOnPageChangeListener(onPageChangeListener)
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
        tracks.setOnClickListener(null)
        podcasts.setOnClickListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager.adapter = null
    }

    private fun changeLibraryPage(page: LibraryPage) {
        viewModel.setLibraryPage(page)
        (requireActivity().findInContext<HasBottomNavigation>()).navigate(BottomNavigationPage.LIBRARY)
    }

    private fun createMediaId(): MediaIdCategory? {
        return pagerAdapter.getCategoryAtPosition(viewPager.currentItem)
    }

    private fun startServiceOrRequestOverlayPermission() {
        featureBubbleNavigator.startServiceOrRequestOverlayPermission(activity!!)
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

    override fun provideLayoutId(): Int = R.layout.fragment_library
}