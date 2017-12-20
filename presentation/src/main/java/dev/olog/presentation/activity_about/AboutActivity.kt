package dev.olog.presentation.activity_about

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
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
        switcher.setInAnimation(this, R.anim.slide_up_fade_in)
        switcher.setOutAnimation(this, R.anim.slide_up_fade_out)
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

    private val factory = ViewSwitcher.ViewFactory {
        val textView = TextView(this@AboutActivity)
        textView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        textView.gravity = Gravity.CENTER_HORIZONTAL
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.setTextColor(ColorStateList.valueOf(
                ContextCompat.getColor(this@AboutActivity, R.color.text_color_primary)))
        textView
    }
}