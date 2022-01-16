package dev.olog.feature.library.folder.tree.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.base.adapter.OptionalAdapter
import dev.olog.feature.library.R
import dev.olog.shared.android.extensions.inflate

class FolderTreeFragmentBackAdapter(
    private val onClick: () -> Unit
) : OptionalAdapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_folder_tree_back)
        view.setOnClickListener { onClick() }
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }
}