package dev.olog.presentation.activity_preferences.neural_network

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.shared_android.neural.NeuralImages
import kotlinx.android.synthetic.main.item_neural_network_preview.view.*
import javax.inject.Inject

class NeuralNetworkImageChoiserAdapter @Inject constructor()
    : RecyclerView.Adapter<NeuralNetworkImageChoiserAdapter.Holder>() {

    private val data = mutableListOf<Int>()

    init {
        for (i in 0 until NeuralImages.NUM_STYLES){
            data.add(i)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = R.layout.item_neural_network_preview

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val uri = Uri.parse("file:///android_asset/thumbnails/style$position.webp")

        GlideApp.with(holder.itemView.context)
                .load(uri)
                .placeholder(android.R.color.black)
                .override(300)
                .into(holder.itemView.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        return Holder(view)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view)

}