package dev.olog.image.provider.fetcher

internal class NoNetworkAllowedException : RuntimeException("not allowed to make network request")