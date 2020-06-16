package com.example.fitappka.database


object ExerciseListRepository{
    private var exerciseList = mutableListOf<String>()

    fun add(exerciseName: String): List<String> = exerciseList.apply{add(exerciseName)}

    fun remove(exerciseName: String): List<String> = exerciseList.apply{remove(exerciseName)}

    fun fetchAll(): List<String> = exerciseList
}