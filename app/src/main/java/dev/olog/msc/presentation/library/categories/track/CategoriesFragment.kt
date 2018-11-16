package dev.olog.msc.presentation.library.categories.track

import android.os.Bundle
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.catchNothing
import dev.olog.msc.floating.window.service.FloatingWindowHelper
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.tutorial.TutorialTapTarget
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.toggleVisibility
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_library_categories.*
import kotlinx.android.synthetic.main.fragment_library_categories.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CategoriesFragment : BaseFragment() {

    companion object {
        const val TAG = "CategoriesFragment"

        @JvmStatic
        fun newInstance(): CategoriesFragment {
            return CategoriesFragment()
        }
    }

    @Inject lateinit var pagerAdapter: CategoriesViewPager
    @Inject lateinit var presenter : CategoriesFragmentPresenter
    @Inject lateinit var navigator: Navigator

    private var floatingWindowTutorialDisposable: Disposable? = null

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.viewPager.adapter = pagerAdapter
        view.tabLayout.setupWithViewPager(view.viewPager)
        view.viewPager.currentItem = presenter.getViewPagerLastPage(pagerAdapter.count)
        view.viewPager.offscreenPageLimit = 3

        view.pagerEmptyState.toggleVisibility(pagerAdapter.isEmpty(), true)
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
    }

    override fun onPause() {
        super.onPause()
        viewPager.removeOnPageChangeListener(onPageChangeListener)
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
        floatingWindowTutorialDisposable.unsubscribe()
    }

    private fun createMediaId(): MediaIdCategory? {
        return pagerAdapter.getCategoryAtPosition(viewPager.currentItem)
    }

    private fun startServiceOrRequestOverlayPermission(){
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!)
    }

    private val onPageChangeListener = object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            presenter.setViewPagerLastPage(position)
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_library_categories
}