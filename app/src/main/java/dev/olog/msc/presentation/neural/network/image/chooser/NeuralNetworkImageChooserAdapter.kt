package dev.olog.msc.presentation.neural.network.image.chooser

import android.app.AlertDialog
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.presentation.GlideApp
import dev.olog.msc.presentation.neural.network.NeuralNetworkActivityViewModel
import dev.olog.msc.utils.k.extension.clearThenAdd
import kotlinx.android.synthetic.main.item_neural_network_preview.view.*

class NeuralNetworkImageChooserAdapter(
        private val dialog: AlertDialog,
        private val viewModel: NeuralNetworkActivityViewModel

) : RecyclerView.Adapter<NeuralNetworkImageChooserAdapter.Holder>() {

    private val data = mutableListOf<Album>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        val holder = Holder(view)
        view.setOnClickListener {
            val position = holder.adapterPosition
            viewModel.updateCurrentNeuralImage(data[position].image)
            dialog.dismiss()
        }
        return holder
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = R.layout.item_neural_network_preview

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val image = data[position].image
        GlideApp.with(holder.itemView.context)
                .load(Uri.parse(image))
                .placeholder(android.R.color.black)
                .override(300)
                .into(holder.itemView.image)
    }

    fun updateData(list: List<Album>){
        data.clearThenAdd(list)
        notifyDataSetChanged()
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view)
}