package dev.olog.feature.base.adapter.media

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.base.R
import dev.olog.feature.base.adapter.OptionalAdapter
import dev.olog.shared.android.extensions.inflate

class ShuffleAdapter(
    private val onClick: () -> Unit,
) : OptionalAdapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_media_shuffle)
        view.setOnClickListener {
            onClick()
        }
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemViewType(position: Int): Int = R.layout.item_media_shuffle

}