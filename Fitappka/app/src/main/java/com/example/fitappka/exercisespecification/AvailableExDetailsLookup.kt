package com.example.fitappka.exercisespecification

import android.view.MotionEvent
import androidx.cardview.widget.CardView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

final class AvailableExDetailsLookup : ItemDetailsLookup<Long>() {

    private lateinit var recyclerView : RecyclerView

    public fun setRecyclerView (reView: RecyclerView){
        recyclerView = reView
    }


    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {

        val cardView = recyclerView.findChildViewUnder(e.x, e.y)
        return if (cardView != null) {
            val cardViewHolder = recyclerView.getChildViewHolder(cardView) as AvailableExercisesRecycleViewAdapter.ViewHolder
            cardViewHolder.getItemDetails()
        } else null
    }


}