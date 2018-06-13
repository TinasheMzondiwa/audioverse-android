package com.tinashe.audioverse.utils.custom

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.audioverse.R
import com.tinashe.audioverse.utils.inflateView

class RecyclerSectionItemDecoration(private val headerOffset: Int,
                                    private val sticky: Boolean,
                                    private val sectionCallback: SectionCallback) : RecyclerView.ItemDecoration() {

    private var headerView: View? = null
    private var header: TextView? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val pos = parent.getChildAdapterPosition(view)
        if (sectionCallback.isSection(pos)) {
            outRect.top = headerOffset
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        if (headerView == null) {
            headerView = inflateView(R.layout.view_header, parent, false)
            header = (headerView as View).findViewById<View>(R.id.tv_header) as TextView
            fixLayoutSize(headerView as View, parent)
        }

        var previousHeader: CharSequence = ""
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            val title = sectionCallback.getSectionHeader(position)
            header?.text = title

            if (previousHeader != title || sectionCallback.isSection(position)) {
                drawHeader(c, child, headerView as View)
                previousHeader = title
            }
        }
    }

    private fun drawHeader(c: Canvas, child: View, headerView: View) {
        c.save()
        if (sticky) {
            c.translate(0f, Math.max(0, child.top - headerView.height).toFloat())
        } else {
            c.translate(0f, (child.top - headerView.height).toFloat())
        }
        headerView.draw(c)
        c.restore()
    }


    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width,
                View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height,
                View.MeasureSpec.UNSPECIFIED)

        val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.paddingLeft + parent.paddingRight,
                view.layoutParams.width)
        val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.paddingTop + parent.paddingBottom,
                view.layoutParams.height)

        view.measure(childWidth, childHeight)

        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    interface SectionCallback {

        fun isSection(position: Int): Boolean

        fun getSectionHeader(position: Int): CharSequence
    }

}
