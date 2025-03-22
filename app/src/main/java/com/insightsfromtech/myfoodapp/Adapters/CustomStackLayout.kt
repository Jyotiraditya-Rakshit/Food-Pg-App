package com.insightsfromtech.myfoodapp.Adapters

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import com.insightsfromtech.myfoodapp.Fragments.HomeFragment

class CustomStackLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var adapter: StackAdapter? = null

    var onStackEmptyListener: onStackEmpty? = null

    interface onStackEmpty{
        fun characterShowcase()
    }

    fun setAdapter(adapter: StackAdapter) {
        this.adapter = adapter
        removeAllViews()
        for (i in 0 until adapter.getItemCount().coerceAtMost(3)) { // Show top 3 cards
            addView(adapter.createView(this, i))
        }
    }

    fun removeTopCard() {
        if (childCount == 0) return

        val topCard = getChildAt(childCount - 1)
        val slideOutAnim = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right)

        slideOutAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                removeView(topCard)
                if(childCount == 0) onStackEmptyListener?.characterShowcase()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        topCard.startAnimation(slideOutAnim)
    }


}