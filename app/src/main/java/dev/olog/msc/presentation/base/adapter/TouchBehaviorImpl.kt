//package dev.olog.msc.presentation.base.adapter
//
//import android.content.Context
//import android.support.v7.widget.helper.ItemTouchHelper
//
//class TouchBehaviorImpl(
//        context: Context?,
//        private val dataController: TouchBehaviorCapabilities,
//        private val touchCallbackConfig: TouchCallbackConfig,
//        canSwipe: Boolean
//
//) : ItemTouchHelperAdapter {
//
//    override val draggableViewType: Int = touchCallbackConfig.draggableViewType
//
//    init {
//        if (draggableViewType == 0) {
//            throw IllegalStateException("try with a valid viewType")
//        }
//    }
//
//    val callback = ItemTouchHelperCallback(context, this, canSwipe)
//    val touchHelper = ItemTouchHelper(callback)
//
//    override fun onItemMove(from: Int, to: Int) {
//        val (realFrom, realTo) = dataController.swap(from, to)
//        val headers = dataController.headersWithinList(from, draggableViewType)
//        touchCallbackConfig.onDragAction.invoke(
//                realFrom - headers,
//                realTo - headers)
//    }
//
//    override fun onItemDismiss(position: Int) {
//        touchCallbackConfig.onSwipeAction.invoke(position)
//        dataController.remove(position)
//    }
//}