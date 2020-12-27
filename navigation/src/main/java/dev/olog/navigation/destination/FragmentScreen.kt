package dev.olog.navigation.destination

private const val PREFIX = "dev.olog"

enum class FragmentScreen(val tag: String) {
    LIBRARY("$PREFIX.library");

    companion object {
        const val OWNERSHIP = PREFIX
    }

}