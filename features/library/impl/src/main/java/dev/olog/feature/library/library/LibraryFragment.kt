package dev.olog.feature.library.library

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.feature.base.BaseFragment
import dev.olog.feature.dialogs.FeatureDialogsNavigator
import dev.olog.feature.floating.FeatureFloatingNavigator
import dev.olog.feature.library.LibraryPage
import dev.olog.feature.library.LibraryPrefs
import dev.olog.feature.library.R
import dev.olog.feature.library.library.LibraryFragmentViewModel.Event.ChangePage
import dev.olog.feature.library.library.LibraryFragmentViewModel.Event.ShowFloatingWindowTutorial
import dev.olog.feature.main.BottomNavigationPage
import dev.olog.feature.main.HasBottomNavigation
import dev.olog.shared.android.extensions.collectOnLifecycle
import dev.olog.shared.android.extensions.requireArgument
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.exhaustive
import dev.olog.shared.lazyFast
import dev.olog.shared.widgets.TutorialTapTarget
import kotlinx.android.synthetic.main.fragment_library.*
import javax.inject.Inject

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.fragment_library) {

    companion object {
        val TAG_TRACK = LibraryFragment::class.java.name
        val TAG_PODCAST = LibraryFragment::class.java.name + ".podcast"
        const val CATEGORY = "category"

        fun newInstance(type: MediaStoreType): LibraryFragment {
            return LibraryFragment().withArguments(CATEGORY to type)
        }
    }

    private val viewModel by viewModels<LibraryFragmentViewModel>()

    @Inject
    lateinit var dialogNavigator: FeatureDialogsNavigator
    @Inject
    lateinit var floatingNavigator: FeatureFloatingNavigator

    private val mediaStoretype by requireArgument<MediaStoreType>(CATEGORY)

    private val pagerAdapter by lazyFast {
        LibraryFragmentAdapter(
            context = requireActivity().applicationContext,
            fragmentManager = childFragmentManager,
            categories = viewModel.getCategories(mediaStoretype),
            type = mediaStoretype,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = viewModel.getViewPagerLastPage(pagerAdapter.count, mediaStoretype)
        viewPager.offscreenPageLimit = 5

        pagerEmptyState.isVisible = pagerAdapter.isEmpty()
        podcasts.isVisible = viewModel.canShowPodcasts()

        tracks.isSelected = mediaStoretype == MediaStoreType.Song
        podcasts.isSelected = mediaStoretype == MediaStoreType.Podcast

        viewModel.events
            .collectOnLifecycle(this) { event ->
                when (event) {
                    is ShowFloatingWindowTutorial -> TutorialTapTarget.floatingWindow(floatingWindow)
                    is ChangePage -> {
                        (requireActivity() as HasBottomNavigation).navigate(BottomNavigationPage.LIBRARY)
                    }
                }.exhaustive
            }

        more.setOnClickListener { dialogNavigator.toMainPopup(requireActivity(), it, createMediaId(), mediaStoretype) }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }

        tracks.setOnClickListener { viewModel.updateLibraryPage(LibraryPage.TRACKS) }
        podcasts.setOnClickListener { viewModel.updateLibraryPage(LibraryPage.PODCASTS) }
    }

    override fun onResume() {
        super.onResume()
        viewPager.addOnPageChangeListener(onPageChangeListener)
    }

    override fun onPause() {
        super.onPause()
        viewPager.removeOnPageChangeListener(onPageChangeListener)
    }

    private fun createMediaId(): MediaUri.Category {
        return pagerAdapter.getCategoryAtPosition(viewPager.currentItem)
    }

    private fun startServiceOrRequestOverlayPermission() {
        floatingNavigator.startService(requireActivity())
    }

    private val onPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {}

        override fun onPageSelected(position: Int) {
            viewModel.setViewPagerLastPage(position, mediaStoretype)
        }
    }

}