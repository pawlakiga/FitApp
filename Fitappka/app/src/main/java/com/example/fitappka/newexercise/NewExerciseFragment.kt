package com.example.fitappka.newexercise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.room.Room
import com.example.fitappka.databinding.FragmentExerciseNewBinding
import com.example.fitappka.R
import com.example.fitappka.database.Exercise
import com.example.fitappka.database.FitappkaDatabase
import com.example.fitappka.database.FitappkaDatabaseDao
import kotlin.concurrent.thread
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay


class NewExerciseFragment: Fragment(){
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentExerciseNewBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_exercise_new, container, false
        )

        ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.training_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.trainingType.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.measure_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.measureType.adapter = adapter
        }
        //var args = NewExerciseFragmentArgs.fromBundle(arguments!!)

        binding.newExToCalibrationButton.setOnClickListener {
            when (binding.measureType.selectedItem) {
                "Czas" -> {

                    lateinit var exercise :Exercise
                        thread {
                            val database =
                                FitappkaDatabase.getInstance(requireActivity().applicationContext)
                            exercise = Exercise(
                                0,
                                binding.newExName.text.toString(),
                                binding.measureType.selectedItem.toString(),
                                binding.trainingType.selectedItem.toString(),
                                null
                            )
                            database.fitappkaDatabaseDao.insertExercise(exercise)
                        }
                    view?.let { it1 -> Snackbar.make(it1,"Pomyślnie dodano ćwiczenie :)", Snackbar.LENGTH_LONG).show() }
                    view?.findNavController()?.navigate(NewExerciseFragmentDirections.actionNewExerciseFragmentToMainMenuFragment())

                }
                "Powtórzenia" -> {
                    val args : Array<String> = arrayOf(
                         binding.newExName.text.toString(),
                         binding.measureType.selectedItem.toString(),
                         binding.trainingType.selectedItem.toString(),
                         binding.newExSensorPlacement.text.toString())
                    view?.findNavController()?.navigate(NewExerciseFragmentDirections.actionNewExerciseFragmentToExerciseCalibrationFragment(args))
                }
            }
        }

        return binding.root
    }




}