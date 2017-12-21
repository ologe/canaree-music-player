package dev.olog.presentation.activity_about

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.ViewSwitcher
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseActivity
import kotlinx.android.synthetic.main.activity_about.*
import javax.inject.Inject

class AboutActivity : BaseActivity() {

    private lateinit var layoutManager : LinearLayoutManager
    @Inject lateinit var adapter: AboutActivityAdapter
    @Inject lateinit var presenter: AboutActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        layoutManager = LinearLayoutManager(this)
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setHasFixedSize(true)

        switcher.setFactory(factory)
        switcher.setCurrentText(getString(R.string.about))
        setInAnimation()
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        setInAnimation()
    }

    override fun onResume() {
        super.onResume()
        adapter.updateDataSet(presenter.data)
        back.setOnClickListener { onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun onBackPressed() {
        setOutAnimation()
        val stack = supportFragmentManager.backStackEntryCount
        if (stack == 1){
            switcher.setText(getString(R.string.about))
        }

        super.onBackPressed()
    }

    private val factory = ViewSwitcher.ViewFactory {
        val textView = TextView(this@AboutActivity)
        textView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.setTextColor(ColorStateList.valueOf(
                ContextCompat.getColor(this@AboutActivity, R.color.text_color_primary)))
        textView
    }

    fun setInAnimation(){
        setSwitcherAnimation(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
    }

    fun setOutAnimation(){
        setSwitcherAnimation(R.anim.slide_in_top, R.anim.slide_out_top)
    }

    private fun setSwitcherAnimation(inAnimation: Int, outAnimation: Int){
        val inAnim = AnimationUtils.loadAnimation(this, inAnimation)
        val outAnima = AnimationUtils.loadAnimation(this, outAnimation)
        switcher.inAnimation = inAnim
        switcher.outAnimation = outAnima
    }

}