package dev.olog.presentation.activity_about

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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

        header.setCurrentText(getString(R.string.about))
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
}