package dev.olog.msc.presentation.library.categories

import android.os.Bundle
import android.view.View
import android.widget.TextView
import dev.olog.core.MediaIdCategory
import dev.olog.msc.R
import dev.olog.msc.catchNothing
import dev.olog.msc.floating.window.service.FloatingWindowHelper
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.tutorial.TutorialTapTarget
import dev.olog.msc.presentation.utils.lazyFast
import dev.olog.msc.utils.k.extension.act
import dev.olog.msc.utils.k.extension.getArgument
import dev.olog.msc.utils.k.extension.unsubscribe
import dev.olog.msc.utils.k.extension.withArguments
import dev.olog.presentation.interfaces.HasBottomNavigation
import dev.olog.presentation.main.BottomNavigationPage
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.textColorPrimary
import dev.olog.shared.textColorSecondary
import dev.olog.shared.toggleVisibility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_library_categories.*
import kotlinx.android.synthetic.main.fragment_library_categories.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CategoriesFragment : BaseFragment() {

    companion object {
        val TAG_TRACK = CategoriesFragment::class.java.name
        val TAG_PODCAST = CategoriesFragment::class.java.name + ".podcast"
        val IS_PODCAST = "IS_PODCAST"

        @JvmStatic
        fun newInstance(isPodcast: Boolean): CategoriesFragment {
            return CategoriesFragment().withArguments(
                    IS_PODCAST to isPodcast
            )
        }
    }

    @Inject
    lateinit var presenter: CategoriesFragmentPresenter
    @Inject
    lateinit var navigator: Navigator

    private val isPodcast by lazyFast { getArgument<Boolean>(IS_PODCAST) }

    private val pagerAdapter by lazyFast {
        CategoriesAdapter(
                act.applicationContext, childFragmentManager, presenter.getCategories(isPodcast)
        )
    }

    private var floatingWindowTutorialDisposable: Disposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = presenter.getViewPagerLastPage(pagerAdapter.count, isPodcast)

        pagerEmptyState.toggleVisibility(pagerAdapter.isEmpty(), true)

        val selectedView : TextView = if (!isPodcast) tracks else podcasts
        val unselectedView : TextView = if (!isPodcast) podcasts else tracks
        selectedView.setTextColor(requireContext().textColorPrimary())
        unselectedView.setTextColor(requireContext().textColorSecondary())
    }

    override fun onResume() {
        super.onResume()
        viewPager.addOnPageChangeListener(onPageChangeListener)
        more.setOnClickListener { catchNothing { navigator.toMainPopup(it, createMediaId()) } }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }

        floatingWindowTutorialDisposable = presenter.showFloatingWindowTutorialIfNeverShown()
                .delay(2, TimeUnit.SECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ TutorialTapTarget.floatingWindow(floatingWindow) }, {})

        tracks.setOnClickListener { navigate(BottomNavigationPage.SONGS) }
        podcasts.setOnClickListener { navigate(BottomNavigationPage.PODCASTS) }
    }

    override fun onPause() {
        super.onPause()
        viewPager.removeOnPageChangeListener(onPageChangeListener)
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
        floatingWindowTutorialDisposable.unsubscribe()
        tracks.setOnClickListener(null)
        podcasts.setOnClickListener(null)
    }

    private fun createMediaId(): MediaIdCategory? {
        return pagerAdapter.getCategoryAtPosition(viewPager.currentItem)
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!)
    }

    private val onPageChangeListener = object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            presenter.setViewPagerLastPage(position, isPodcast)
        }
    }

    private fun navigate(page: BottomNavigationPage) {
        (requireActivity() as HasBottomNavigation).navigate(page)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_library_categories
}