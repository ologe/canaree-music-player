package dev.olog.presentation.fragment_about

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import kotlinx.android.synthetic.main.fragment_about.view.*
import javax.inject.Inject

class AboutFragment : BaseFragment() {

    companion object {
        const val TAG = "AboutFragment"
    }

    private lateinit var layoutManager : LinearLayoutManager
    @Inject lateinit var adapter: AboutFragmentAdapter
    @Inject lateinit var presenter: AboutFragmentPresenter

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.layoutManager = layoutManager
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()
        adapter.updateDataSet(presenter.data)
        view!!.back.setOnClickListener { activity!!.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        view!!.back.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_about
}