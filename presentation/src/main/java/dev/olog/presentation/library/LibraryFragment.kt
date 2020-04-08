package dev.olog.presentation.library

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import dev.olog.analytics.TrackerFacade
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.feature.presentation.base.model.PresentationIdCategory
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.interfaces.HasBottomNavigation
import dev.olog.presentation.model.BottomNavigationPage
import dev.olog.presentation.model.LibraryPage
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.popup.main.MainPopupCategory
import dev.olog.presentation.popup.main.toMainPopupCategory
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.shared.android.extensions.getArgument
import dev.olog.shared.android.extensions.textColorPrimary
import dev.olog.shared.android.extensions.textColorSecondary
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import dev.olog.shared.mandatory
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.coroutines.delay
import javax.inject.Inject

class LibraryFragment : BaseFragment() {

    companion object {
        @JvmStatic
        val TAG_TRACK = LibraryFragment::class.java.name
        @JvmStatic
        val TAG_PODCAST = LibraryFragment::class.java.name + "_podcast"
        const val IS_PODCAST = "is_podcast"

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
    internal lateinit var navigator: Navigator
    @Inject
    lateinit var trackerFacade: TrackerFacade

    private val isPodcast by lazyFast {
        getArgument<Boolean>(IS_PODCAST)
    }

    private val pagerAdapter by lazyFast {
        LibraryFragmentAdapter(
            requireActivity().applicationContext, childFragmentManager, presenter.getCategories(isPodcast)
        )
    }

    fun isCurrentFragmentFolderTree(): Boolean {
        return pagerAdapter.getCategoryAtPosition(viewPager.currentItem) == PresentationIdCategory.FOLDERS &&
                pagerAdapter.showFolderAsHierarchy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        removeFolderFragment()

        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = presenter.getViewPagerLastPage(pagerAdapter.count, isPodcast)

        pagerEmptyState.isVisible = pagerAdapter.isEmpty()

        val selectedView: TextView = if (!isPodcast) tracks else podcasts
        val unselectedView: TextView = if (!isPodcast) podcasts else tracks
        selectedView.setTextColor(requireContext().textColorPrimary())
        unselectedView.setTextColor(requireContext().textColorSecondary())

        if (!presenter.canShowPodcasts()){
            podcasts.isVisible = false
        }

        if (presenter.showFloatingWindowTutorialIfNeverShown()) {
            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                delay(500) // TODO try
                TutorialTapTarget.floatingWindow(floatingWindow)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewPager.addOnPageChangeListener(onPageChangeListener)
        more.setOnClickListener {
            navigator.toMainPopup(it, createPopupCategory())
        }
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

    /**
     * Since viewpager has problems with changing items, folder fragment has to be
     * removed in order to allow the change between folder fragment types
     */
    private fun removeFolderFragment() {
        val index = pagerAdapter.findFolderFragment()
        mandatory(index >= 0) ?: return
        val tag = pagerAdapter.tagFor(index)
        mandatory(tag != null) ?: return
        val fragment = childFragmentManager.findFragmentByTag(tag)
        mandatory(fragment != null) ?: return

        childFragmentManager.commitNow(true) {
            remove(fragment!!)
        }
    }

    private fun changeLibraryPage(page: LibraryPage) {
        presenter.setLibraryPage(page)
        (requireActivity() as HasBottomNavigation).navigate(BottomNavigationPage.LIBRARY)
    }

    private fun createPopupCategory(): MainPopupCategory {
        return pagerAdapter.getCategoryAtPosition(viewPager.currentItem).toMainPopupCategory()
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(requireActivity())
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
                val category = pagerAdapter.getCategoryAtPosition(position)
                trackerFacade.trackScreen(category.toString(), null)

            }
        }

    override fun provideLayoutId(): Int = R.layout.fragment_library
}