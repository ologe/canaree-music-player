package dev.olog.msc.presentation.library.categories

import android.support.v4.view.ViewPager
import javax.inject.Inject

class CategoriesOnPageChangeListener @Inject constructor(
        private val func: (Int) -> Unit

) : ViewPager.OnPageChangeListener {

    override fun onPageSelected(position: Int) {
        func(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

}