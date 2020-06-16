package com.example.fitappka.traininginprogress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fitappka.R
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitappka.database.FitappkaDatabase
import com.example.fitappka.databinding.FragmentTrainingSelectionBinding
import kotlin.concurrent.thread

class TrainingSelectionFragment : Fragment() {

    private lateinit var viewModel: TrainingProgressViewModel
    private val availableTrainingsRecyclerAdapter : AvailableTrainingsRecyclerAdapter
            = AvailableTrainingsRecyclerAdapter(mutableListOf())
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel  = ViewModelProviders.of(requireActivity()).get(TrainingProgressViewModel::class.java)
        availableTrainingsRecyclerAdapter.setViewModel(viewModel)
        thread { viewModel.onResume() }
        viewModel.trainingsWithExercises.observe(viewLifecycleOwner,
            Observer  {
                availableTrainingsRecyclerAdapter.updateAvailableTrainings(it)
            }
            )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentTrainingSelectionBinding  = DataBindingUtil.inflate(inflater, R.layout.fragment_training_selection, container, false)
        binding.lifecycleOwner = this
        val linearLayoutManager = LinearLayoutManager(this.requireContext())
        binding.availableTrainingsRecycler.apply {
            adapter = availableTrainingsRecyclerAdapter
            layoutManager = linearLayoutManager
        }
        binding.trainingSelectedButton.setOnClickListener{
            if (availableTrainingsRecyclerAdapter.selectedTrainingPosition != -1 ) {
                viewModel.setSelectedTrainingPosition(availableTrainingsRecyclerAdapter.selectedTrainingPosition)
                view?.findNavController()?.navigate(TrainingSelectionFragmentDirections.actionTrainingSelectionFragmentToTrainingInProgressFragment())
            }
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        thread { viewModel.onResume() }
        availableTrainingsRecyclerAdapter.selectedTrainingPosition = -1
    }



}