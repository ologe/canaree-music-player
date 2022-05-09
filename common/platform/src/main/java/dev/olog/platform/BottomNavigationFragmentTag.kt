package dev.olog.platform

fun interface BottomNavigationFragmentTag {

    operator fun invoke(): String

}

fun Iterable<BottomNavigationFragmentTag>.containsTag(tag: String?): Boolean {
    return any { it() == tag }
}