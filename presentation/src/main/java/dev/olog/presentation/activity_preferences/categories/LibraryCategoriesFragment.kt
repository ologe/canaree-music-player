package dev.olog.presentation.activity_preferences.categories

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseDialogFragment
import dev.olog.presentation.utils.extension.makeDialog
import javax.inject.Inject

class LibraryCategoriesFragment : BaseDialogFragment() {

    companion object {
        const val TAG = "LibraryCategoriesFragment"

        fun newInstance(): LibraryCategoriesFragment {
            return LibraryCategoriesFragment()
        }
    }

    @Inject lateinit var presenter: LibraryCategoriesFragmentPresenter
    private lateinit var adapter: LibraryCategoriesFragmentAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view : View = inflater.inflate(R.layout.dialog_list, null, false)

        val builder = AlertDialog.Builder(context)
                .setTitle("Library categories") // todo resources
                .setView(view)
                .setNeutralButton("Reset", null)
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_save, { _, _ ->
                    presenter.setDataSet(adapter.data)
                    activity!!.setResult(Activity.RESULT_OK)
                })

        val list = view.findViewById<RecyclerView>(R.id.list)
        adapter = LibraryCategoriesFragmentAdapter(presenter.getDataSet().toMutableList())
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)
        adapter.touchHelper.attachToRecyclerView(list)

        val dialog = builder.makeDialog()

        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
                    val defaultData = presenter.getDefaultDataSet()
                    adapter.updateDataSet(defaultData)
                }

        return dialog
    }

}