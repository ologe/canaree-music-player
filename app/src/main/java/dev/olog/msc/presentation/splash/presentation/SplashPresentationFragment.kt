package dev.olog.msc.presentation.splash.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.olog.msc.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashPresentationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash_presentation, container, false)
    }

    override fun onResume() {
        super.onResume()
        root.setOnClickListener { activity!!.viewPager.currentItem = 1 }
    }

    override fun onPause() {
        super.onPause()
        root.setOnClickListener(null)
    }
}