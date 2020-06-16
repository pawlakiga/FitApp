package com.example.fitappka.exercisespecification

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.fitappka.R
import com.example.fitappka.database.Exercise
import com.example.fitappka.database.FitappkaDatabaseDao
import com.example.fitappka.newtraining.NewTrainingViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.withContext

class AvailableExercisesRecycleViewAdapter(exercises : MutableList<Exercise>): RecyclerView.Adapter<AvailableExercisesRecycleViewAdapter.ViewHolder>(){

    private var availableExList = exercises
    private lateinit var viewModel : NewTrainingViewModel
   /* private var tracker: SelectionTracker<Long>? = null
    public val sTracker : SelectionTracker<Long>?
        get() = tracker
*/
    init{
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
     fun setViewModel(model: NewTrainingViewModel) {
         viewModel = model
     }

    class ViewHolder(cardView: View) : RecyclerView.ViewHolder(cardView) {
        val exerciseInfo: TextView = itemView.findViewById(R.id.exercise_info)
        val exName : TextView = itemView.findViewById(R.id.ex_name)
        val exCardBackground: MaterialCardView = itemView.findViewById<MaterialCardView>(R.id.available_ex_card_background)
        var isSelected : Boolean = false
        val cardDefaultBackground = exCardBackground.background
        fun getItemDetails() : ItemDetailsLookup.ItemDetails<Long> {
            return object : ItemDetailsLookup.ItemDetails<Long> () {
                override fun getSelectionKey(): Long? = itemId
                override fun getPosition(): Int = adapterPosition
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val exerciseCardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.full_ex_card, parent, false)
        return ViewHolder(exerciseCardView)
    }

    fun getItem(position: Int) : Exercise = availableExList[position]
    fun getPosition(exercise : Exercise) = availableExList.indexOf(exercise)

    override fun getItemCount(): Int = availableExList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        availableExList[position].apply {
            val info: String = exerciseBPType + "\n" + exerciseTRType
            val name: String = exerciseId.toString() + ". " + exerciseName
            holder.exerciseInfo.text = info
            holder.exName.text = name
        }

        holder.itemView.findViewById<Button>(R.id.delete_ex_button).setOnClickListener {
            //viewModel.removeExercise(availableExList.value!![position].exerciseName)
            viewModel.deleteExercise(availableExList[position].exerciseId)
            viewModel.setAvailableExercises()
            //this.notifyItemRemoved(position)
        }

        holder.itemView.setOnClickListener {
            if (viewModel.exerciseSelectedPosition == -1) {
                viewModel.exerciseSelectedPosition = position
                holder.exCardBackground.background = ColorDrawable(Color.parseColor("#7f00ff"))
            } else if (viewModel.exerciseSelectedPosition == position) {
                viewModel.exerciseSelectedPosition = -1
                holder.exCardBackground.background = holder.cardDefaultBackground
            } else {
                holder.itemView.let { it1 -> Snackbar.make(it1,"Proszę odznaczyć poprzednio wybrane ćwiczenie", Snackbar.LENGTH_LONG).show() }
            }
        }

    }

    fun setAvailableExercises(exercises : List<Exercise>){
        availableExList = exercises as MutableList<Exercise>
        notifyDataSetChanged()
    }
   /* public fun setTracker(t : SelectionTracker<Long>) {
        tracker = t
    }*/
}