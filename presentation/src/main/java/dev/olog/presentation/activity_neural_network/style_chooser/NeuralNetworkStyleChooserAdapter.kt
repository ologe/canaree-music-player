package dev.olog.presentation.activity_neural_network.style_chooser

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation.activity_neural_network.NeuralNetworkActivityViewModel
import dev.olog.shared_android.neural.NeuralImages
import kotlinx.android.synthetic.main.item_neural_network_preview.view.*

class NeuralNetworkStyleChooserAdapter(
        private val dialog: AlertDialog,
        private val viewModel: NeuralNetworkActivityViewModel

) : RecyclerView.Adapter<NeuralNetworkStyleChooserAdapter.Holder>() {

    private val data = mutableListOf<Int>()

    init {
        for (i in 0 until NeuralImages.NUM_STYLES){
            data.add(i)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = R.layout.item_neural_network_preview

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val uri = NeuralImages.getThumbnail(position)

        GlideApp.with(holder.itemView.context)
                .load(uri)
                .placeholder(android.R.color.black)
                .override(300)
                .into(holder.itemView.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        val holder = Holder(view)
        view.setOnClickListener {
            val position = holder.adapterPosition
            viewModel.updateCurrentNeuralStyle(position)
            dialog.dismiss()
        }
        return holder
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view)

}