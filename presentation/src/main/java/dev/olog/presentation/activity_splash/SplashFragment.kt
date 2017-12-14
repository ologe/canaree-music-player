package dev.olog.presentation.activity_splash

import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.fragment_splash.view.*

class SplashFragment : BaseFragment() {

    override fun onResume() {
        super.onResume()
        view!!.root.setOnClickListener { activity!!.viewPager.currentItem = 1 }
    }

    override fun onPause() {
        super.onPause()
        view!!.root.setOnClickListener {  }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_splash
}