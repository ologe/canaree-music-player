package dev.olog.msc.presentation.library.categories

import android.os.Bundle
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.FloatingInfoServiceHelper
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.navigator.Navigator
import kotlinx.android.synthetic.main.fragment_library_categories.*
import kotlinx.android.synthetic.main.fragment_library_categories.view.*
import javax.inject.Inject

class CategoriesFragment : BaseFragment() {

    companion object {
        const val TAG = "CategoriesFragment"

        fun newInstance(): CategoriesFragment {
            return CategoriesFragment()
        }
    }

    @Inject lateinit var pagerAdapter: CategoriesViewPager
    @Inject lateinit var presenter : CategoriesFragmentPresenter
    @Inject lateinit var navigator: Navigator

    private val onPageChangeListener by lazy(LazyThreadSafetyMode.NONE) {
        CategoriesOnPageChangeListener { presenter.setViewPagerLastPage(it) } }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.viewPager.adapter = pagerAdapter
        view.tabLayout.setupWithViewPager(view.viewPager)
        view.viewPager.currentItem = presenter.getViewPagerLastPage(pagerAdapter.count)
    }

    override fun onResume() {
        super.onResume()
        viewPager.addOnPageChangeListener(onPageChangeListener)
        search.setOnClickListener { navigator.toSearchFragment() }
        settings.setOnClickListener { navigator.toMainPopup(it) }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
    }

    override fun onPause() {
        super.onPause()
        viewPager.removeOnPageChangeListener(onPageChangeListener)
        search.setOnClickListener(null)
        settings.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
    }

    fun startServiceOrRequestOverlayPermission(){
//        if (billing.isPremium()){
        FloatingInfoServiceHelper.startServiceOrRequestOverlayPermission(activity!!)
//        } else {
//            toast("floating window is a premium feature")
//             todo open purchase activity
//        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_library_categories
}