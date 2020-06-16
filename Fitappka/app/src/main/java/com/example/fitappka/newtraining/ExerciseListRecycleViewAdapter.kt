package com.example.fitappka.newtraining

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater

import android.widget.ImageButton
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.example.fitappka.R

class ExerciseListRecycleViewAdapter(var exerciseList: List<String>, val exercisesListener: (String) -> Unit): RecyclerView.Adapter<ExerciseListRecycleViewAdapter.ViewHolder>(){

    class ViewHolder(cardView: View) : RecyclerView.ViewHolder(cardView) {
        val exerciseName: TextView = itemView.findViewById<TextView>(R.id.exercise_element)
        val removeExerciseButton = itemView.findViewById<ImageButton>(R.id.remove_exercise_element)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val exerciseCardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_card, parent, false)
        return ViewHolder(exerciseCardView)
    }

    override fun getItemCount(): Int = exerciseList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        exerciseList[position].apply{
            holder.exerciseName.text = exerciseList[position]
            holder.removeExerciseButton.setOnClickListener{exercisesListener(this)}
        }
    }
}