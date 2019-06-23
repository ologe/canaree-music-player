package dev.olog.msc.presentation.special.thanks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.olog.msc.R
import dev.olog.shared.lazyFast
import dev.olog.shared.act
import dev.olog.shared.extensions.asLiveData
import dev.olog.shared.extensions.subscribe
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.fragment_special_thanks.view.*

class SpecialThanksFragment : Fragment() {

    companion object {
        const val TAG = "SpecialThanksFragment"
    }

    private val presenter by lazyFast { SpecialThanksPresenter(act.applicationContext) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_special_thanks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layoutManager = LinearLayoutManager(context)
        val adapter = SpecialThanksFragmentAdapter(lifecycle)
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
        act.switcher?.setText(getString(R.string.about_special_thanks_to))
    }
}