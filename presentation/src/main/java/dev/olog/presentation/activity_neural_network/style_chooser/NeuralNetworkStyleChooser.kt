package dev.olog.presentation.activity_neural_network.style_chooser

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseDialogFragment
import dev.olog.presentation.activity_neural_network.NeuralNetworkActivityViewModel
import dev.olog.presentation.utils.extension.makeDialog
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
                .setTitle("choice an image to blend") // todo resources
                .setView(view)
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_save, null)

        val list = view.findViewById<RecyclerView>(R.id.list)

        val dialog = builder.makeDialog()

        adapter = NeuralNetworkStyleChooserAdapter(dialog, viewModel)
        list.adapter = adapter
        list.layoutManager = GridLayoutManager(context, 2)

        return dialog
    }

}