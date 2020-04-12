package dev.olog.lib.image.loader.fetcher

internal class NoNetworkAllowedException : RuntimeException("not allowed to make network request")