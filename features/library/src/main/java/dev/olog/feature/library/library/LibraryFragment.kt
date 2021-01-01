package dev.olog.feature.library.library

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.feature.base.HasBottomNavigation
import dev.olog.feature.library.LibraryTutorial
import dev.olog.feature.library.R
import dev.olog.feature.library.dialog.MainPopupDialog
import dev.olog.navigation.BottomNavigationPage
import dev.olog.navigation.Navigator
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.fragment_library) {

    companion object {
        fun newInstance(isPodcast: Boolean): LibraryFragment {
            return LibraryFragment().withArguments(
                Params.IS_PODCAST to isPodcast
            )
        }
    }

    private val viewModel by viewModels<LibraryFragmentViewModel>()

    @Inject
    lateinit var navigator: Navigator

    @Inject
    internal lateinit var mainPopupFactory: Provider<MainPopupDialog>

    private val isPodcast by argument<Boolean>(Params.IS_PODCAST)

    private val pagerAdapter by lazyFast {
        LibraryFragmentAdapter(
            context = requireContext(),
            fragmentManager = childFragmentManager,
            categories = viewModel.getCategories()
        )
    }

    fun isCurrentFragmentFolderTree(): Boolean { // TODO
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
        viewPager.currentItem = viewModel.getViewPagerLastPage(pagerAdapter.count)
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
            launch {
                delay(500)
                LibraryTutorial.floatingWindow(floatingWindow)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewPager.addOnPageChangeListener(onPageChangeListener)
        more.setOnClickListener(this::toMainPopup)
        floatingWindow.setOnClickListener { navigator.toFloatingWindow() }

        tracks.setOnClickListener { changeLibraryPage(BottomNavigationPage.LIBRARY_TRACKS) }
        podcasts.setOnClickListener { changeLibraryPage(BottomNavigationPage.LIBRARY_PODCASTS) }
    }

    override fun onPause() {
        super.onPause()
        viewPager.removeOnPageChangeListener(onPageChangeListener)
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
        tracks.setOnClickListener(null)
        podcasts.setOnClickListener(null)
    }

    private fun toMainPopup(view: View) {
        val mediaId = createMediaId() ?: return
        mainPopupFactory.get().show(view, mediaId)
    }

    private fun changeLibraryPage(page: BottomNavigationPage) {
        viewModel.setLibraryPage(page)
        (requireActivity() as HasBottomNavigation).navigate(page)
    }

    private fun createMediaId(): MediaIdCategory? {
        return pagerAdapter.getCategoryAtPosition(viewPager.currentItem)
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
                viewModel.setViewPagerLastPage(position)
            }
        }

}