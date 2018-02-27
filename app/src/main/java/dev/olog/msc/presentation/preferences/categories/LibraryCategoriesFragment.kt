package dev.olog.msc.presentation.preferences.categories

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseDialogFragment
import dev.olog.msc.presentation.base.adapter.TouchHelperAdapterCallback
import dev.olog.msc.utils.k.extension.makeDialog
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
                .setTitle(R.string.prefs_library_categories_title)
                .setView(view)
                .setNeutralButton(R.string.popup_neutral_reset, null)
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_save, null)

        val list = view.findViewById<RecyclerView>(R.id.list)
        adapter = LibraryCategoriesFragmentAdapter(presenter.getDataSet().toMutableList())
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)

        val callback = TouchHelperAdapterCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(list)
        adapter.touchHelper = touchHelper

        val dialog = builder.makeDialog()

        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
                    val defaultData = presenter.getDefaultDataSet()
                    adapter.updateDataSet(defaultData)
                }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    presenter.setDataSet(adapter.data)
                    activity!!.setResult(Activity.RESULT_OK)
                    dismiss()
                }

        return dialog
    }

}