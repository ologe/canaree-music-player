package dev.olog.msc.presentation.library.categories.podcast

import android.os.Bundle
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.catchNothing
import dev.olog.msc.floating.window.service.FloatingWindowHelper
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.MediaIdCategory
import kotlinx.android.synthetic.main.fragment_library_categories.*
import kotlinx.android.synthetic.main.fragment_library_categories.view.*
import javax.inject.Inject

class CategoriesPodcastFragment : BaseFragment() {

    companion object {
        const val TAG = "CategoriesPodcastFragment"

        @JvmStatic
        fun newInstance(): CategoriesPodcastFragment {
            return CategoriesPodcastFragment()
        }
    }

    @Inject lateinit var pagerAdapter: CategoriesPodcastFragmentViewPager
    @Inject lateinit var navigator: Navigator

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.viewPager.adapter = pagerAdapter
        view.tabLayout.setupWithViewPager(view.viewPager)
        view.header.text = getString(R.string.common_podcast)
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener { catchNothing { navigator.toMainPopup(it, createMediaId()) } }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
    }

    private fun createMediaId(): MediaIdCategory {
        return when (viewPager.currentItem){
            0 -> MediaIdCategory.PODCASTS
            else -> MediaIdCategory.PODCASTS_PLAYLIST
        }
    }

    override fun onPause() {
        super.onPause()
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
    }

    private fun startServiceOrRequestOverlayPermission(){
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_library_categories
}