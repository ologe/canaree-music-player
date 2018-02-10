package dev.olog.msc.presentation.neural.network.style.chooser

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseDialogFragment
import dev.olog.msc.presentation.neural.network.NeuralNetworkActivityViewModel
import dev.olog.msc.utils.k.extension.makeDialog
import javax.inject.Inject

class NeuralNetworkStyleChooser : BaseDialogFragment() {

    companion object {
        const val TAG = "NeuralNetworkImageChoiser"

        fun newInstance(): NeuralNetworkStyleChooser {
            return NeuralNetworkStyleChooser()
        }
    }

    private lateinit var adapter : NeuralNetworkStyleChooserAdapter
    @Inject
    lateinit var viewModel: NeuralNetworkActivityViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)
        val view : View = inflater.inflate(R.layout.dialog_list, null, false)

        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.neural_pick_style)
                .setView(view)

        val list = view.findViewById<RecyclerView>(R.id.list)

        val dialog = builder.makeDialog()

        adapter = NeuralNetworkStyleChooserAdapter(dialog, viewModel)
        list.adapter = adapter
        list.layoutManager = GridLayoutManager(context, 2)

        return dialog
    }

}