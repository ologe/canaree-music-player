package dev.olog.presentation.activity_preferences.blacklist

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
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
import org.jetbrains.anko.toast
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
                .setTitle(R.string.prefs_blacklist_title)
                .setMessage(R.string.prefs_blacklist_description)
                .setView(view)
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_save, null)

        val list = view.findViewById<RecyclerView>(R.id.list)
        adapter = BlacklistFragmentAdapter()
        list.adapter = adapter
        list.layoutManager = GridLayoutManager(context, 3)

        val dialog = builder.makeDialog()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    val allIsBlacklisted = adapter.data.all { it.isBlacklisted }
                    if (allIsBlacklisted){
                        showErrorMessage()
                    } else {
                        presenter.setDataSet(adapter.data)
                        activity!!.setResult(Activity.RESULT_OK)
                        dismiss()
                    }
                }

        return dialog
    }

    private fun showErrorMessage(){
        activity!!.toast(R.string.prefs_blacklist_error)
    }

}