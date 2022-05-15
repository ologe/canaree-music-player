package dev.olog.platform.navigation

import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

object FragmentTagFactory {

    private const val PREFIX = "tag.factory"
    // fragment tag, last added
    private val backStackCount = mutableMapOf<String, Int>()

    fun create(clazz: KClass<out Fragment>): String {
        return "$PREFIX.${clazz.java.simpleName}"
    }

    fun createWithBackStack(base: String): String {
        return createBackStackTag(base)
    }

    fun isFromFactory(tag: String?) : Boolean {
        return tag?.startsWith(PREFIX) == true
    }

    fun isFromViewPager(tag: String?): Boolean {
        return tag?.startsWith("android:switcher:") == true
    }

    /**
     * Use this when you can instantiate multiple times same fragment
     */
    private fun createBackStackTag(fragmentTag: String): String {
        // get last + 1
        val counter = backStackCount.getOrPut(fragmentTag) { 0 } + 1
        // update
        backStackCount[fragmentTag] = counter
        // creates new
        return "$fragmentTag$counter"
    }

}