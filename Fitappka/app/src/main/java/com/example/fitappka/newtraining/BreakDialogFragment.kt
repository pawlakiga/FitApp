package com.example.fitappka.newtraining

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.fitappka.R

class BreakDialogFragment : DialogFragment() {

    private lateinit var breakDialogViewModel: BreakDialogViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        // AlertDialog - Break Counter
        val builder = AlertDialog.Builder(activity!!)
        val i: LayoutInflater = activity!!.layoutInflater
        val customView: View = i.inflate(R.layout.break_dialog, null)

        breakDialogViewModel = ViewModelProviders.of(activity!!).get(BreakDialogViewModel::class.java)

        builder.setPositiveButton("Ustaw") { dialog, id ->
                breakDialogViewModel.setFlag()
            }
            .setNegativeButton(
                "Anuluj"
            ) { dialog, id -> }


        val counterText = customView.findViewById<TextView>(R.id.counter_text)

        breakDialogViewModel.counter.observe(this, Observer {
            counterText.text = breakDialogViewModel.counter.value.toString()
        })

        customView.findViewById<ImageButton>(R.id.increment_counter_button)?.setOnClickListener {
            breakDialogViewModel.increment()
        }

        customView.findViewById<ImageButton>(R.id.decrement_counter_button)?.setOnClickListener {
            breakDialogViewModel.decrement()
        }

        builder.setView(customView)
        // Create the AlertDialog object and return it
        return builder.create()
    }

}

// Notes

/*
DialogFragment Lifecycle

onAttach
onCreate
onCreateDialog
onCreateView
onActivityCreated
onStart
onResume

onPause
onStop
onDestroyView
onDestroy
onDetach
 */