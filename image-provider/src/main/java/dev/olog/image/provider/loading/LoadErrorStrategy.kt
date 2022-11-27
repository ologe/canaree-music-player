package dev.olog.image.provider.loading

sealed interface LoadErrorStrategy {
    object None : LoadErrorStrategy
    object Full : LoadErrorStrategy
    object Gradient : LoadErrorStrategy
}