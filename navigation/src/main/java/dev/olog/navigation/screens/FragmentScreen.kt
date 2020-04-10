package dev.olog.navigation.screens

// TODO can be used in scrollHelper#hasFragmentOwnership
private const val PREFIX = "dev.olog"

enum class FragmentScreen(val tag: String) {
    LIBRARY_TRACKS("$PREFIX.library.track"),
    LIBRARY_PODCAST("$PREFIX.library.podcast"),
    SEARCH("$PREFIX.search"),
    QUEUE("$PREFIX.queue"),
    DETAIL("$PREFIX.detail"),
    PLAYER("$PREFIX.player"),
    PLAYER_MINI("$PREFIX.player.mini"),
    SETTINGS("$PREFIX.settings"),
    ABOUT("$PREFIX.about"),
    ONBOARDING("$PREFIX.onboarding"),
    EQUALIZER("$PREFIX.equalizer");

    companion object {
        const val OWNERSHIP = PREFIX
    }

}