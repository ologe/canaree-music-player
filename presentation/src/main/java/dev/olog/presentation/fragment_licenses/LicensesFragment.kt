package dev.olog.presentation.fragment_licenses

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.fragment_licenses.view.*
import javax.inject.Inject

class LicensesFragment : BaseFragment(){

    companion object {
        const val TAG = "LicensesFragment"
    }

    @Inject lateinit var presenter: LicensesFragmentPresenter
    @Inject lateinit var adapter: LicensesFragmentAdapter
    private lateinit var layoutManager : LinearLayoutManager

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
    }

    override fun onResume() {
        super.onResume()
        adapter.updateDataSet(presenter.data)
        activity!!.switcher.setText(getString(R.string.about_third_sw))
    }

    override fun provideLayoutId(): Int = R.layout.fragment_licenses
}