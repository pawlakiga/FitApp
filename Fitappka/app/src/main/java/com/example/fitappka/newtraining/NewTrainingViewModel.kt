package com.example.fitappka.newtraining

// Debug purposes
import android.util.Log

// ViewModel structure
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitappka.database.*

class NewTrainingViewModel: ViewModel(){

    private val fitappkaRepository = FitappkaRepository
    var selectedBPType : Int = 0
    var trainingName : String = ""

    private val exercisesListRepository: ExerciseListRepository = ExerciseListRepository
    private val _exercisesList = MutableLiveData<List<String>>()
    val exercisesList: LiveData<List<String>>
        get() = _exercisesList

    private var _addedExercises : MutableList<Array<Int>> = mutableListOf()
    val addedExercises : MutableList<Array<Int>>
            get() = _addedExercises

    var exerciseSelectedPosition : Int = -1

    private var _availableExercisesList = MutableLiveData<List<Exercise>>()
    val availableExercisesList : LiveData<List<Exercise>>
        get() = _availableExercisesList

    fun setAvailableExercises () {
        _availableExercisesList.value =fitappkaRepository.getAllExercises()
    }

    fun addExercise(exerciseName: String){
        _exercisesList.value = exercisesListRepository.add(exerciseName)
    }

    fun addToTraining( position: Int, measure : Int ) {
        _addedExercises.add(arrayOf(position,measure))
    }

    fun removeExercise(exerciseName: String){
        _exercisesList.value = exercisesListRepository.remove(exerciseName)
        val toRemove  = _availableExercisesList.value!!.indexOf(_availableExercisesList.value!!.find { exercise -> exercise.exerciseName == exerciseName} )
        _addedExercises.remove(_addedExercises.find { array -> array[0] == toRemove })
        refreshExercises() // Necessary? Not sure
    }

    fun deleteExercise(exerciseId: Long){
        val toRemove = _availableExercisesList.value!!.find { exercise -> exercise.exerciseId == exerciseId}!!
        val idToRemove  = _availableExercisesList.value!!.indexOf(toRemove)
        _addedExercises.remove(_addedExercises.find { array -> array[0] == idToRemove })
        fitappkaRepository.deleteExercise(toRemove)
        setAvailableExercises() // Necessary? Not sure
    }

    fun onResume() {
        refreshExercises()
        exerciseSelectedPosition = -1

    }

    fun refreshExercises(){
        _exercisesList.value = exercisesListRepository.fetchAll()
    }


    fun saveTraining (trainingName : String, trainingBPtype : String) {

        val training: Training = Training(
            0,
            trainingName,
            trainingBPtype
        )

        fitappkaRepository.insertTraining(training)

        val trainingId = fitappkaRepository.getLatestTrainingId()
        for (order in 0..addedExercises.lastIndex) {
            val exerciseId = availableExercisesList.value!![addedExercises[order][0]].exerciseId
            val crossRef = TrainingExerciseCrossRef (
                trainingId,
                exerciseId,
                addedExercises.lastIndex-order,
                -1,
                addedExercises[order][1]
            )
            fitappkaRepository.insertTrainingExerciseCrossRef(crossRef)
        }

    }


}