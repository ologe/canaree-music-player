package dev.olog.shared.compose.extension

import androidx.compose.foundation.pager.PagerState

val PagerState.visiblePageOffset
    get() = when {
        currentPage == targetPage -> {
            if (currentPageOffsetFraction == 0f) {
                0f
            } else if (currentPageOffsetFraction < 0) {
                currentPageOffsetFraction + 1f
            } else {
                currentPageOffsetFraction
            }
        }
        currentPage < targetPage -> currentPageOffsetFraction
        else -> currentPageOffsetFraction + 1f
    }

val PagerState.visiblePage
    get() = when {
        visiblePageOffset < .5f -> currentPage
        else -> currentPage - 1
    }