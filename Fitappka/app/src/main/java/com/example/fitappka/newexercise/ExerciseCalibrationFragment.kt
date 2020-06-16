package com.example.fitappka.newexercise

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import com.example.fitappka.MainActivity.Companion.database
import com.example.fitappka.R
import com.example.fitappka.bluetooth.BlunoLibrary
import com.example.fitappka.database.Exercise
import com.example.fitappka.database.FitappkaDatabase
import com.example.fitappka.databinding.FragmentExerciseCalibrationBinding
import com.example.fitappka.databinding.FragmentExerciseNewBinding
import com.google.android.material.snackbar.Snackbar
import com.pigielwpam.bluetooth2.BluetoothData
import kotlin.concurrent.thread

class ExerciseCalibrationFragment : BlunoLibrary() {

    private lateinit var binding : FragmentExerciseCalibrationBinding
    private val bluetoothData : BluetoothData = BluetoothData()
    private var reps = 0
    private var exName  :String = ""
    private lateinit var mainContext : Context
    private var callibrated : Boolean = false


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
      //  mainContext = requireContext()
        //super.onCreateProcess()

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        mainContext = requireContext()
        super.setContext(mainContext)
        onCreateProcess()
        binding = DataBindingUtil.inflate<FragmentExerciseCalibrationBinding >(
            inflater, R.layout.fragment_exercise_calibration, container, false
        )

        val args = ExerciseCalibrationFragmentArgs.fromBundle(requireArguments())
        binding.calibrationExDetails.run{setText("Połącz się z czujnikiem lub dotknij ekranu, żeby zakończyć.")}
        exName = args.exerciseData[0]
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 1
        )

        serialBegin(115200)

        binding.calibrationScanButton.setOnClickListener {
/*
            lateinit var exercises : List<Exercise>
            var names = ""
                val database = FitappkaDatabase.getInstance(requireActivity().applicationContext)
                exercises = database.fitappkaDatabaseDao.getAllExercises()

            if (!exercises.isEmpty()) {
                for (i in 0..exercises.lastIndex) {
                    names = names + "\n" + exercises[i].exerciseId.toString() + "  " +exercises[i].exerciseName
                }
                binding.calibrationExDetails.run { text = names}
            }

            val exercise = Exercise(
                0, args.exerciseData[0],
                args.exerciseData[1],
                args.exerciseData[2],
                args.exerciseData[3]
            )
            thread {
                database.fitappkaDatabaseDao.insertExercise(exercise)
            }
            view?.let { it1 -> Snackbar.make(it1,"Pomyślnie dodano ćwiczenie :)", Snackbar.LENGTH_LONG).show() }
            view?.findNavController()?.navigate(ExerciseCalibrationFragmentDirections.actionExerciseCalibrationFragmentToMainMenuFragment())
*/
            val permissionCheck = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                val requestCheck =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity() ,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                if (requestCheck) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ), 1
                    )
                } else {
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Permission Required")
                        .setMessage("Please enable location permission to use this application.")
                        .setNeutralButton("I Understand", null)
                        .show()
                }
            } else {
                buttonScanOnClickProcess() //Alert Dialog for selecting the BLE device
            }

        }

        binding.callibrationBackground.setOnClickListener {
            if (callibrated == true || binding.calibrationScanButton.text == "Skanuj") {
                val exercise = Exercise(
                    0, args.exerciseData[0],
                    args.exerciseData[1],
                    args.exerciseData[2],
                    args.exerciseData[3]
                )
                thread {
                    database?.fitappkaDatabaseDao?.insertExercise(exercise)
                }
                view?.let { it1 -> Snackbar.make(it1,"Pomyślnie dodano ćwiczenie :)", Snackbar.LENGTH_LONG).show() }
                view?.findNavController()?.navigate(ExerciseCalibrationFragmentDirections.actionExerciseCalibrationFragmentToMainMenuFragment())
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        println("BlUNOActivity onResume")
        onResumeProcess() //onResume Process by BlunoLibrary
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onActivityResultProcess(
            requestCode,
            resultCode,
            data
        ) //onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        super.onPause()
        onPauseProcess() //onPause Process by BlunoLibrary
    }

    override fun onStop() {
        super.onStop()
        onStopProcess() //onStop Process by BlunoLibrary
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyProcess() //onDestroy Process by BlunoLibrary
    }

    override fun onConectionStateChange(theConnectionState: connectionStateEnum?) { //Once connection state changes, this function will be called
        when (theConnectionState) {
            connectionStateEnum.isConnected -> binding.calibrationScanButton.text = "Połączono"
            connectionStateEnum.isConnecting -> binding.calibrationScanButton.text = "Łączę"
            connectionStateEnum.isToScan -> binding.calibrationScanButton.text = "Skanuj"
            connectionStateEnum.isScanning -> binding.calibrationScanButton.text = "Skanuję"
            connectionStateEnum.isDisconnecting -> binding.calibrationScanButton.text = "Rozłączam"
            else -> {
            }
        }
    }

    override fun onSerialReceived(theString: String?) {                            //Once connection data received, this function will be called
        // TODO Auto-generated method stub
        // binding.calibrationExDetails.text = theString//append the text into the EditText


            if (theString != null && theString.contains(';') && theString.split(';').lastIndex == 2 &&
                theString.contains('\r') && theString.indexOf(';') != 0
            ) {
                val string2 = theString.substring(0, theString.lastIndexOf('\r'))
                if (bluetoothData.getNumAngles(string2, 3)) {
                    binding.calibrationExDetails.run { setText("Zmierzone! Dotknij ekranu, żeby zakończyć :)") }
                    val reps = bluetoothData.getReps()
                    //bluetoothData.getSchema(reps)
                    //bluetoothData.saveSchemaToFile(exName, requireContext())
                    //bluetoothData.readFromFile(exName,requireContext())
                    //binding.calibrationExDetails.textSize = binding.calibrationExDetails.textSize*2
                    callibrated = true
                }/*
                if (bluetoothData.repFinished()) {
                    reps += 1
                    binding.calibrationExDetails.text = reps.toString()
                }*/
            }

        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
        //(binding.serialReveicedText.parent as ScrollView).fullScroll(View.FOCUS_DOWN)
    }



}