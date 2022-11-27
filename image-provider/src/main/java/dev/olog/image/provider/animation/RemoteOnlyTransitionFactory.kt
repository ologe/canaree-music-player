package dev.olog.image.provider.animation

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.TransitionFactory

// run transition only for downloaded images, other loads are quick
internal class RemoteOnlyTransitionFactory : TransitionFactory<Drawable> {

    private val crossfadeFactory = DrawableCrossFadeFactory.Builder().build()

    override fun build(
        dataSource: DataSource,
        isFirstResource: Boolean
    ): Transition<Drawable>? {
        if (dataSource == DataSource.REMOTE) {
            return crossfadeFactory.build(dataSource, isFirstResource)
        }
        return null
    }
}