package dev.olog.feature.presentation.base.adapter

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.presentation.base.R
import kotlinx.android.extensions.LayoutContainer

// TODO test lifecycle
class DataBoundViewHolder(
    view: View
) : RecyclerView.ViewHolder(view),
    LifecycleOwner,
    LayoutContainer {

    private val lifecycleRegistry = LifecycleRegistry(this)

    val imageView : ImageView? = itemView.findViewById(R.id.cover)

    override val containerView: View = itemView

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
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

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

}