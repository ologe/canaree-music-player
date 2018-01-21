///*
// * Copyright 2016 Google Inc. All Rights Reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package dev.olog.floating_info.api
//
//import android.view.View
//import android.view.View.GONE
//import android.view.ViewGroup
//import android.view.WindowManager
//import android.widget.RelativeLayout
//import org.jetbrains.anko.dip
//import java.util.*
//
///**
// * The visual area occupied by a [HoverView]. A `Screen` acts as a factory for the
// * visual elements used within a `HoverView`.
// */
//internal class Screen(
//        private val mContainer: RelativeLayout
//
//) {
//
//    val contentDisplay: ContentDisplay = ContentDisplay(mContainer.context)
//    val exitView: ExitView = ExitView(mContainer.context)
//    val shadeView: ShadeView = ShadeView(mContainer.context)
//    private val mTabs = HashMap<String, FloatingTab>()
//    private var mIsDebugMode = false
//
//    val width: Int
//        get() = mContainer.width
//
//    val height: Int
//        get() = mContainer.height
//
//    init {
//        mContainer.addView(shadeView, WindowManager.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//        ))
//        mContainer.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//
//        shadeView.hideImmediate()
//
//        mContainer.addView(exitView, WindowManager.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//        ))
//        exitView.visibility = GONE
//
//        mContainer.addView(contentDisplay)
//        contentDisplay.visibility = GONE
//    }
//
//    fun createChainedTab(sectionId: HoverMenu.SectionId, tabView: View): FloatingTab {
//        val tabId = sectionId.toString()
//        return createChainedTab(tabId, tabView)
//    }
//
//    private fun createChainedTab(tabId: String, tabView: View): FloatingTab {
//        if (mTabs.containsKey(tabId)) {
//            return mTabs[tabId]!!
//        } else {
//            val chainedTab = FloatingTab(mContainer.context, tabId)
//            chainedTab.setTabView(tabView)
//            chainedTab.enableDebugMode(mIsDebugMode)
//            mTabs[tabId] = chainedTab
//            return chainedTab
//        }
//    }
//
//    fun getChainedTab(sectionId: HoverMenu.SectionId?): FloatingTab? {
//        val tabId = sectionId?.toString()
//        return getChainedTab(tabId)
//    }
//
//    fun getChainedTab(tabId: String?): FloatingTab? {
//        return mTabs[tabId]
//    }
//
//    fun destroyChainedTab(chainedTab: FloatingTab) {
//        mTabs.remove(chainedTab.tabId)
//        chainedTab.setTabView(null)
//        mContainer.removeView(chainedTab)
//    }
//}
