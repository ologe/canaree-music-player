package dev.olog.presentation.activity_preferences.blacklist

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseDialogFragment
import dev.olog.presentation.utils.extension.makeDialog
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.shared_android.extension.asLiveData
import javax.inject.Inject

class BlacklistFragment : BaseDialogFragment() {

    companion object {
        const val TAG = "BlacklistFragment"

        fun newInstance(): BlacklistFragment {
            return BlacklistFragment()
        }
    }

    @Inject lateinit var presenter: BlacklistFragmentPresenter
    private lateinit var adapter : BlacklistFragmentAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.data.asLiveData()
                .subscribe(this, adapter::updateDataSet)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view : View = inflater.inflate(R.layout.dialog_list, null, false)

        val builder = AlertDialog.Builder(context)
                .setTitle("Blacklist") // todo resources
                .setMessage("Click to show/hide")
                .setView(view)
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_save, { _, _ ->
                    // todo
                    activity!!.setResult(Activity.RESULT_OK)
                })

        val list = view.findViewById<RecyclerView>(R.id.list)
        adapter = BlacklistFragmentAdapter()
        list.adapter = adapter
        list.layoutManager = GridLayoutManager(context, 3)

        return builder.makeDialog()
    }

}