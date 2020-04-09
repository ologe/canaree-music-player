package dev.olog.presentation.navigator

// TODO made all internal
const val NEXT_REQUEST_THRESHOLD: Long = 400 // ms

// fragment tag, last added
var backStackCount = mutableMapOf<String, Int>()

private var lastRequest: Long = -1

/**
 * Use this when you can instantiate multiple times same fragment
 */
fun createBackStackTag(fragmentTag: String): String {
    // get last + 1
    val counter = backStackCount.getOrPut(fragmentTag) { 0 } + 1
    // update
    backStackCount[fragmentTag] = counter
    // creates new
    return "$fragmentTag$counter"
}

fun allowed(): Boolean {
    val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
    lastRequest = System.currentTimeMillis()
    return allowed
}

