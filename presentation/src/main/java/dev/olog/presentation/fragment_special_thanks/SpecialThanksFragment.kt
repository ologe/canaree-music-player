package dev.olog.presentation.fragment_special_thanks

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import kotlinx.android.synthetic.main.fragment_special_thanks.view.*
import javax.inject.Inject

class SpecialThanksFragment : BaseFragment() {

    companion object {
        const val TAG = "SpecialThanksFragment"
    }

    @Inject lateinit var presenter: SpecialThanksPresenter
    @Inject lateinit var adapter: SpecialThanksFragmentAdapter
    private lateinit var layoutManager : LinearLayoutManager

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
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

    override fun provideLayoutId(): Int = R.layout.fragment_special_thanks
}