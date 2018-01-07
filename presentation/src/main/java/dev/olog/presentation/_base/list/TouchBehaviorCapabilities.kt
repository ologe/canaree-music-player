package dev.olog.presentation._base.list

interface TouchBehaviorCapabilities {

    fun swap(from: Int, to: Int): Pair<Int, Int>

    fun remove(position: Int)

    fun headersWithinList(position: Int, viewType: Int) : Int

}