package dev.olog.msc.presentation.library.categories.podcast

import android.os.Bundle
import android.view.View
import dev.olog.core.MediaIdCategory
import dev.olog.msc.R
import dev.olog.msc.catchNothing
import dev.olog.msc.floating.window.service.FloatingWindowHelper
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.utils.lazyFast
import dev.olog.msc.utils.k.extension.act
import dev.olog.shared.toggleVisibility
import kotlinx.android.synthetic.main.fragment_library_categories.*
import kotlinx.android.synthetic.main.fragment_library_categories.view.*
import javax.inject.Inject

class CategoriesPodcastFragment : BaseFragment() {

    companion object {
        val TAG = CategoriesPodcastFragment::class.java.name

        @JvmStatic
        fun newInstance(): CategoriesPodcastFragment {
            return CategoriesPodcastFragment()
        }
    }

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var presenter : CategoriesPodcastFragmentPresenter

    private val pagerAdapter by lazyFast { CategoriesPodcastFragmentAdapter(
        act.applicationContext, childFragmentManager, presenter.getCategories()
    ) }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.viewPager.adapter = pagerAdapter
        view.tabLayout.setupWithViewPager(view.viewPager)
        view.header.text = getString(R.string.common_podcasts)
        view.viewPager.currentItem = presenter.getViewPagerLastPage(pagerAdapter.count)

        view.pagerEmptyState.toggleVisibility(pagerAdapter.isEmpty(), true)
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener { catchNothing { navigator.toMainPopup(it, createMediaId()) } }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
        viewPager.addOnPageChangeListener(onPageChangeListener)
    }

    private fun createMediaId(): MediaIdCategory? {
        return pagerAdapter.getCategoryAtPosition(viewPager.currentItem)
    }

    override fun onPause() {
        super.onPause()
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
        viewPager.removeOnPageChangeListener(onPageChangeListener)
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