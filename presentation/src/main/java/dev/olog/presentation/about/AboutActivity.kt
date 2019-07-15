package dev.olog.presentation.about

import android.os.Bundle
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseActivity
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.presentation.pro.IBilling
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.extensions.subscribe
import kotlinx.android.synthetic.main.activity_about.*
import javax.inject.Inject

class AboutActivity : BaseActivity() {

    @Inject
    lateinit var navigator: NavigatorAbout
    @Inject
    lateinit var billing: IBilling
    private val presenter by lazyFast {
        AboutActivityPresenter(
            applicationContext,
            billing
        )
    }
    private val adapter by lazyFast {
        AboutActivityAdapter(
            lifecycle,
            navigator,
            presenter
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        switcher?.setFactory(factory)
        switcher?.setCurrentText(getString(R.string.about))
        setInAnimation()

        presenter.observeData()
            .subscribe(this, adapter::updateDataSet)

    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        setInAnimation()
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun onBackPressed() {
        setOutAnimation()
        val stack = supportFragmentManager.backStackEntryCount
        if (stack == 1) {
            switcher?.setText(getString(R.string.about))
        }

        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

    private val factory = ViewSwitcher.ViewFactory {
        val textView = TextView(this@AboutActivity)
        textView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )
        TextViewCompat.setTextAppearance(textView, R.style.Headline6)
        textView.gravity = Gravity.CENTER
        textView
    }

    private fun setInAnimation() {
        setSwitcherAnimation(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
    }

    private fun setOutAnimation() {
        setSwitcherAnimation(R.anim.slide_in_top, R.anim.slide_out_top)
    }

    private fun setSwitcherAnimation(inAnimation: Int, outAnimation: Int) {
        val inAnim = AnimationUtils.loadAnimation(this, inAnimation)
        val outAnim = AnimationUtils.loadAnimation(this, outAnimation)
        switcher?.inAnimation = inAnim
        switcher?.outAnimation = outAnim
    }

}