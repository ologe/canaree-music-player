package dev.olog.msc.presentation.licenses

import android.os.Bundle
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.utils.k.extension.act
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.subscribe
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.fragment_licenses.view.*
import javax.inject.Inject

class LicensesFragment : BaseFragment(){

    companion object {
        const val TAG = "LicensesFragment"
    }

    @Inject lateinit var presenter: LicensesFragmentPresenter
    @Inject lateinit var adapter: LicensesFragmentAdapter

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.adapter = adapter
        view.list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        Single.just(presenter.data)
                .toFlowable()
                .asLiveData()
                .subscribe(viewLifecycleOwner, adapter::updateDataSet)
    }

    override fun onResume() {
        super.onResume()
        act.switcher.setText(getString(R.string.about_third_sw))
    }

    override fun provideLayoutId(): Int = R.layout.fragment_licenses
}