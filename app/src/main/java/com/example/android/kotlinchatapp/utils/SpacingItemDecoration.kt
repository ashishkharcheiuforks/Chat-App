package com.example.android.kotlinchatapp.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration():RecyclerView.ItemDecoration() {
    val paint=Paint()
    init {
        paint.color=Color.LTGRAY
        paint.isAntiAlias=true
    }
//    override fun getItemOffsets(
//        outRect: Rect,
//        view: View,
//        parent: RecyclerView,
//        state: RecyclerView.State
//    ) {
//        outRect.bottom=8
//        outRect.left=8
//        outRect.right=8
//        outRect.top=8
//    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left=parent.paddingLeft+50
        val right=parent.width-parent.paddingRight-50
        val childCount=parent.childCount
        for (i in 0 until childCount){
            val child=parent.getChildAt(i)
            val params=child.layoutParams as RecyclerView.LayoutParams
            val top =child.bottom+params.bottomMargin
            val bottom=top+4
            c.drawRect(left.toFloat(),top.toFloat(),right.toFloat(),bottom.toFloat(),paint)
        }
    }
}