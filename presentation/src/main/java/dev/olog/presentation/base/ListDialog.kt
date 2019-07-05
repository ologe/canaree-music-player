package dev.olog.presentation.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.presentation.R
import dev.olog.shared.extensions.ctx

abstract class ListDialog : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view : View = inflater.inflate(provideLayoutId(), null, false)

        val builder = MaterialAlertDialogBuilder(ctx)
            .setView(view)

        setupBuilder(builder)

        val list = view.findViewById<RecyclerView>(R.id.list)
        setupRecyclerView(list)
        val dialog = builder.show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { positiveAction() }
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener { negativeAction() }
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener { neutralAction() }

        return dialog
    }

    protected abstract fun setupBuilder(builder: AlertDialog.Builder):AlertDialog.Builder
    protected abstract fun setupRecyclerView(list: RecyclerView)

    protected abstract fun positiveAction()
    protected open fun negativeAction(){
        dismiss()
    }
    protected open fun neutralAction(){}

    protected open fun provideLayoutId() = R.layout.fragment_list

}