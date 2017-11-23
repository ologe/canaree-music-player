package dev.olog.presentation.model

data class Header(
        val viewType: Int
)

fun Header.toDisplayableItem(position: Int) : DisplayableItem {
    return DisplayableItem(
            this.viewType,
            "header$position",
            "i'm a header"
    )
}