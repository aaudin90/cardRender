package com.aaudin90.glcardview

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GlCardRecyclerView(
    context: Context,
    attrs: AttributeSet? = null,
) : RecyclerView(context, attrs) {

    private val recyclerVisibleRect = Rect()

    init {
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            getGlobalVisibleRect(recyclerVisibleRect)
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (glCardViewUnder(event)) {
            return false
        }
        return super.onInterceptTouchEvent(event)
    }

    private fun glCardViewUnder(event: MotionEvent): Boolean {
        val view: View = findChildViewUnder(event.x, event.y) ?: return false
        if (view is GlCardView) {
            return true
        }
        val touchedGlobalX = event.x.toInt() + recyclerVisibleRect.left
        val touchedGlobalY = event.y.toInt() + recyclerVisibleRect.top
        return glCardViewUnderInHierarchy(view, touchedGlobalX, touchedGlobalY)
    }

    private fun glCardViewUnderInHierarchy(
        rootView: View,
        touchedGlobalX: Int,
        touchedGlobalY: Int
    ): Boolean {
        var view = rootView
        while (view is ViewGroup) {
            val mView = view
            for (i in 0 until mView.childCount) {
                val rect = Rect()
                val child = mView.getChildAt(i)
                child.getGlobalVisibleRect(rect)
                if (rect.contains(touchedGlobalX, touchedGlobalY)) {
                    if (child is GlCardView) {
                        return true
                    } else {
                        view = child
                        break
                    }
                }
            }
        }
        return false
    }
}