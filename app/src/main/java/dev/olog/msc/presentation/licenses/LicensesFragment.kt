package dev.olog.msc.presentation.licenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.olog.msc.R
import dev.olog.shared.extensions.act
import dev.olog.shared.extensions.asLiveData
import dev.olog.shared.extensions.subscribe
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.fragment_licenses.view.*

class LicensesFragment : Fragment(){

    companion object {
        const val TAG = "LicensesFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_licenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val presenter = LicensesFragmentPresenter(act.applicationContext)
        val adapter = LicensesFragmentAdapter(lifecycle)

        view.list.adapter = adapter
        view.list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        Single.just(presenter.data)
                .toFlowable()
                .asLiveData()
                .subscribe(viewLifecycleOwner, adapter::updateDataSet)
    }

    override fun onResume() {
        super.onResume()
        act.switcher?.setText(getString(R.string.about_third_sw))
    }

}