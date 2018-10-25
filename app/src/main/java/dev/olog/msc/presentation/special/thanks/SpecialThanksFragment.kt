package dev.olog.msc.presentation.special.thanks

import android.os.Bundle
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.utils.k.extension.act
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.subscribe
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.fragment_special_thanks.view.*
import javax.inject.Inject

class SpecialThanksFragment : BaseFragment() {

    companion object {
        const val TAG = "SpecialThanksFragment"
    }

    @Inject lateinit var presenter: SpecialThanksPresenter
    @Inject lateinit var adapter: SpecialThanksFragmentAdapter
    private lateinit var layoutManager : androidx.recyclerview.widget.LinearLayoutManager

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)

        Single.just(presenter.data)
                .toFlowable()
                .asLiveData()
                .subscribe(viewLifecycleOwner, adapter::updateDataSet)
    }

    override fun onResume() {
        super.onResume()
        act.switcher.setText(getString(R.string.about_special_thanks_to))
    }

    override fun provideLayoutId(): Int = R.layout.fragment_special_thanks
}