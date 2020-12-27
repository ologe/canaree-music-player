package dev.olog.navigation.destination

private const val PREFIX = "dev.olog"

enum class FragmentScreen(val tag: String) {
    LIBRARY_TRACKS("$PREFIX.library.tracks"),
    LIBRARY_PODCASTS("$PREFIX.library.podcasts"),
    SEARCH("$PREFIX.search"),
    QUEUE("$PREFIX.queue"),

    ONBOARDING("$PREFIX.onboarding"),
    DETAIL("$PREFIX.detail"),
    RELATED_ARTISTS("$PREFIX.related.artists"),
    RECENTLY_ADDED("$PREFIX.recently.added");

    companion object {
        const val OWNERSHIP = PREFIX
    }

}