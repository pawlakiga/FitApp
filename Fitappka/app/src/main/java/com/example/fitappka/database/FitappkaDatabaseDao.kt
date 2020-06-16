package com.example.fitappka.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FitappkaDatabaseDao {

    @Insert
    fun insertExercise(exercise: Exercise)
    @Update
    fun updateExercise(exercise: Exercise)
    @Query("SELECT * FROM exercise_table WHERE exerciseId = :key")
    fun getExercise(key: Long): Exercise
    @Delete
    fun deleteExercise(exercise: Exercise)
    @Query("SELECT * FROM exercise_table ORDER BY exerciseId DESC")
    fun getAllExercises(): List<Exercise>// Not sure if LiveData in our case is proper

    @Insert
    fun insertTraining(training: Training)
    @Update
    fun updateTraining(training: Training)
    @Query("SELECT * FROM training_table WHERE trainingId = :key")
    fun getTraining(key: Long): Training
    @Delete
    fun deleteTraining(training: Training)
    @Query("SELECT * FROM training_table ORDER BY trainingId DESC")
    fun getAllTrainings(): List<Training>// Not sure if LiveData in our case is proper

    @Insert
    fun insertTrainingExerciseCrossRef(trainingExerciseCrossRef: TrainingExerciseCrossRef)

    @Query ( "SELECT * FROM training_exercises_table ORDER BY trainingId" )
    fun getAllCrossRefs(): List<TrainingExerciseCrossRef>

    @Transaction
    @Query("SELECT * FROM training_table")
    fun getTrainingsWithExercises(): List<TrainingWithExercises>

   data class TrainingWithExercises(
        @Embedded public val training: Training,
        @Relation(
            parentColumn = "trainingId",
            entityColumn = "exerciseId",
            associateBy = Junction(TrainingExerciseCrossRef::class)
        )
        public val exercises: List<Exercise>
    )

    @Query ("SELECT * FROM training_exercises_table WHERE trainingId = :key ORDER BY exercise_order")
    fun getCrossRefsInTraining(key: Long) : List<TrainingExerciseCrossRef>



/*    @Transaction
    @Query ("SELECT exerciseOrder,exerciseName,exerciseTRNubmer,exerciseBreakTime" +
            " FROM exercise_table JOIN training_exercises_table" +
            "ON exercise_table.exerciseId = training_exercises_table.exerciseId" +
            "WHERE trainingId= :key" )*/

}