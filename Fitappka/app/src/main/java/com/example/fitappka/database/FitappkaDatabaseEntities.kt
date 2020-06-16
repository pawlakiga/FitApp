package com.example.fitappka.database

import androidx.room.*

@Entity(tableName = "exercise_table")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val exerciseId: Long,

    @ColumnInfo(name = "exercise_name")
    val exerciseName: String,

    @ColumnInfo(name = "exercise_tr_type")
    val exerciseTRType: String,

    @ColumnInfo(name = "exercise_bp_type")
    val exerciseBPType: String,

    @ColumnInfo (name = "exercise_sensor_placement" )
    val exerciseSensorPlacement : String?
)

@Entity(tableName = "training_table")
data class Training(
    @PrimaryKey(autoGenerate = true)
    val trainingId: Long,

    @ColumnInfo (name = "training_name")
    val trainingName: String,

    @ColumnInfo(name = "training_bp_type")
    val trainingBPType: String
)

@Entity(tableName = "training_exercises_table", primaryKeys = ["trainingId", "exerciseId"])
data class TrainingExerciseCrossRef(
    val trainingId: Long,
    val exerciseId: Long,

    @ColumnInfo(name = "exercise_order")
    val exerciseOrder: Int,

    @ColumnInfo(name = "exercise_break_time")
    val exerciseBreakTime: Int = -1,

    @ColumnInfo(name = "exercise_tr_number")
    val exerciseTRNumber: Int
)

