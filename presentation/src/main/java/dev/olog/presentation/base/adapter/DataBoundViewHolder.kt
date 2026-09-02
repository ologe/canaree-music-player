package dev.olog.presentation.base.adapter

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.R
import dev.olog.shared.lazyFast

class DataBoundViewHolder(view: View) : RecyclerView.ViewHolder(view),
    LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    val imageView : ImageView? by lazyFast { itemView.findViewById<ImageView>(R.id.cover) }

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    fun <T : View> findViewById(id: Int): T {
        return itemView.findViewById(id)
    }

    fun onAppear() {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    fun onDisappear() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

}