package com.example.fitappka.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.fitappka.MainActivity

object FitappkaRepository {

    private val fitappkaDao : FitappkaDatabaseDao = MainActivity.database!!.fitappkaDatabaseDao

    public fun insertExercise(exercise : Exercise) {
        fitappkaDao.insertExercise(exercise)
    }

    public fun deleteExercise(exercise: Exercise){
        fitappkaDao.deleteExercise(exercise)
    }

    public fun deleteTraining(training: Training){
        fitappkaDao.deleteTraining(training)
    }

    public fun getAllExercises() : List<Exercise> {
        return fitappkaDao.getAllExercises()
    }

    public fun insertTraining (training: Training) {
        fitappkaDao.insertTraining(training)
    }

    public fun getAllTrainings() : List<Training> {
        return fitappkaDao.getAllTrainings()
    }

    public fun insertTrainingExerciseCrossRef(crossRef: TrainingExerciseCrossRef){
        fitappkaDao.insertTrainingExerciseCrossRef(crossRef)
    }

    fun getCrossRefsInTraining(key : Long): List<TrainingExerciseCrossRef> {
        return fitappkaDao.getCrossRefsInTraining(key)
    }
    fun getLatestTrainingId() : Long {
        return getAllTrainings()[0].trainingId
    }

    fun getTrainingsWithExercises() : List<FitappkaDatabaseDao.TrainingWithExercises> {
        return fitappkaDao.getTrainingsWithExercises()
    }

}
