package com.example.fitappka.menu

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.databinding.DataBindingUtil
import com.example.fitappka.R
import com.example.fitappka.databinding.FragmentMenuMainBinding

import androidx.navigation.findNavController

import com.google.android.material.snackbar.Snackbar


class MainMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // setContentView does not exist in fragment so we inflate
        val binding: FragmentMenuMainBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_menu_main, container, false
        )

        // Button handler - navigate to TrainingSelectionFragment
        binding.doTraining.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(MainMenuFragmentDirections.actionMainMenuFragmentToTrainingSelectionFragment())
        }

        // Button handler - navigate to StatisticsFragment
       /* binding.statistics.setOnClickListener { view: View ->
            view.let { it1 -> Snackbar.make(it1,"W trakcie tworzenia :(", Snackbar.LENGTH_LONG).show() }
        }*/

        // Button handler - navigate to NewTrainingFragment
        binding.newTraining.setOnClickListener { view: View ->
            view.findNavController().navigate(MainMenuFragmentDirections.actionMainMenuFragmentToNewTrainingFragment())
        }

        // Button handler - navigate to NewExerciseFragment
        binding.newExercise.setOnClickListener { view: View ->
            view.findNavController().navigate(MainMenuFragmentDirections.actionMainMenuFragmentToNewExerciseFragment())
        }

        // Button handler - open calendar activity
        binding.plan.setOnClickListener { view: View ->
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "Trening")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Do dzieła!")
            startActivity(intent)
        }

        return binding.root
    }

}
