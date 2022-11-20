package dev.olog.image.provider.loading

import com.bumptech.glide.Priority as GlidePriority

enum class Priority {
    Immediate,
    High,
    Normal,
    Low,
}

internal fun Priority.toGlidePriority(): GlidePriority = when (this) {
    Priority.Immediate -> GlidePriority.IMMEDIATE
    Priority.High -> GlidePriority.HIGH
    Priority.Normal -> GlidePriority.NORMAL
    Priority.Low -> GlidePriority.LOW
}