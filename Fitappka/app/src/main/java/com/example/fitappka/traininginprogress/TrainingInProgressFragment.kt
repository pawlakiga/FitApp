package com.example.fitappka.traininginprogress

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.fitappka.R
import com.example.fitappka.bluetooth.BlunoLibrary
import com.example.fitappka.databinding.FragmentTrainingInProgressBinding
import com.pigielwpam.bluetooth2.BluetoothData
import kotlin.concurrent.thread

class TrainingInProgressFragment : BlunoLibrary() {

    private lateinit var viewModel: TrainingProgressViewModel
    private lateinit var audioManager: AudioManager
    private lateinit var soundPool : SoundPool
    private val streamType  = AudioManager.STREAM_MUSIC
    private val MAX_STREAMS = 5
    private var loaded : Boolean = false
    private var soundIdExDone : Int = 0
    private var volume : Float = 0f
    private lateinit var binding : FragmentTrainingInProgressBinding
    private val bluetoothData : BluetoothData = BluetoothData()
    private lateinit var mainContext : Context
    private var repsDone = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

       /* thread { viewModel.refreshTrainingsWithExercises() }*/

        audioManager = requireActivity().getSystemService(AUDIO_SERVICE) as AudioManager
        val currentVolumeIndex : Int = audioManager.getStreamVolume(streamType)
        val maxVolumeIndex : Int = audioManager.getStreamMaxVolume(streamType)
        volume = maxVolumeIndex.toFloat()
        requireActivity().volumeControlStream = streamType

        val audioAttributes : AudioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_GAME)
            .build()

        val builder : SoundPool.Builder = SoundPool.Builder()
        builder.setAudioAttributes(audioAttributes)
            .setMaxStreams(MAX_STREAMS)
        soundPool = builder.build()

        soundPool.setOnLoadCompleteListener { soundPool: SoundPool, sampleId: Int, status: Int ->
            loaded = true
        }

        soundIdExDone = soundPool.load( requireContext(),R.raw.point_blank, 1)

    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainContext = requireContext()
        super.setContext(mainContext)
        onCreateProcess()

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_training_in_progress,
            container,
            false
        )
        viewModel  = ViewModelProviders.of(requireActivity()).get(TrainingProgressViewModel::class.java)

            viewModel.refreshCrossRefs()
            viewModel.currentExercise = 0
        thread {
            viewModel.refreshTrainingsWithExercises()
        }
        binding.trainingInProgressName.text =
            viewModel.trainingsWithExercises.
            value!![viewModel.selectedTrainingPosition]
                .training.trainingName
        binding.trainingInProgressExName.text =
            viewModel.trainingsWithExercises.value!![viewModel.selectedTrainingPosition].exercises[viewModel.currentExercise].exerciseName
        binding.trainingInProgressExInfo.text =
            viewModel.trainingExerciseCrossRef[viewModel.currentExercise].exerciseTRNumber.toString()

        if (viewModel.isCurrentTimeMeasured()) {
            viewModel.setTimerForExercise()
            viewModel.countdownTimer.secondsLeft.observe(viewLifecycleOwner,
                Observer {
                    binding.trainingInProgressExInfo.text = it.toString()
                    if (it == 0) {
                        playExDoneSound()
                    }
                })
        }

        binding.nextExButton.setOnClickListener{

            viewModel.currentExercise++
            if (viewModel.trainingFinished()) {
                view?.findNavController()?.navigate(TrainingInProgressFragmentDirections.actionTrainingInProgressFragmentToMainMenuFragment())
            } else {
                binding.trainingInProgressName.text =
                    viewModel.trainingsWithExercises.value!![viewModel.selectedTrainingPosition].training.trainingName
                binding.trainingInProgressExName.text =
                    viewModel.trainingsWithExercises.value!![viewModel.selectedTrainingPosition].exercises[viewModel.currentExercise].exerciseName
                binding.trainingInProgressExInfo.text =
                    viewModel.trainingExerciseCrossRef[viewModel.currentExercise].exerciseTRNumber.toString()
                if (viewModel.isCurrentTimeMeasured()) {
                    viewModel.setTimerForExercise()
                    viewModel.countdownTimer.secondsLeft.observe(viewLifecycleOwner,
                        Observer {
                            binding.trainingInProgressExInfo.text = it.toString()
                            if (it == 0) {
                                playExDoneSound()
                            }
                        })
                }
                }
                }

        binding.trainingExitButton.setOnClickListener {
            view?.findNavController()?.navigate(TrainingInProgressFragmentDirections.actionTrainingInProgressFragmentToMainMenuFragment())
        }

        binding.trainingInProgressBackground.setOnClickListener {
            if(viewModel.isCurrentTimeMeasured() && !viewModel.countdownTimer.finished &&
                    viewModel.countdownTimer.secondsLeft.value!! == viewModel.trainingExerciseCrossRef[viewModel.currentExercise].exerciseTRNumber) viewModel.startExTimer()
        }

        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 1
        )

        serialBegin(115200)

        binding.trainingScanButton.setOnClickListener {

            if (!viewModel.isCurrentTimeMeasured()) {

                val permissionCheck = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    val requestCheck =
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
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

        }



        return binding.root

    }

    override fun onResume() {
        super.onResume()
        thread {
            viewModel.refreshCrossRefs()

        }
        viewModel.currentExercise = 0
        onResumeProcess() //onResume Process by BlunoLibrary
    }


    fun playExDoneSound() {
        if (loaded) {
            val leftVolume : Float = volume
            val rightVolume : Float = volume

            val streamId : Int = soundPool
                .play(soundIdExDone, leftVolume, rightVolume, 1, 0, 1f)
        }
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
            connectionStateEnum.isConnected -> binding.trainingScanButton.text = "Połączono"
            connectionStateEnum.isConnecting -> binding.trainingScanButton.text = "Łączę"
            connectionStateEnum.isToScan -> binding.trainingScanButton.text = "Skanuj"
            connectionStateEnum.isScanning -> binding.trainingScanButton.text = "Skanuję"
            connectionStateEnum.isDisconnecting -> binding.trainingScanButton.text = "Rozłączam"
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
            /*if (bluetoothData.getNumAngles(string2, 3)) {
                binding.calibrationExDetails.run { setText("Zmierzone! ") }
                val reps = bluetoothData.getReps()
                bluetoothData.getSchema(reps)
                bluetoothData.saveSchemaToFile(exName, requireContext())
                bluetoothData.readFromFile(exName,requireContext())
                binding.calibrationExDetails.textSize = binding.calibrationExDetails.textSize*2
            }*/

            bluetoothData.getNumAngles(string2,3)

            if (bluetoothData.repFinished()) {
                if (repsDone < viewModel.trainingExerciseCrossRef[viewModel.currentExercise].exerciseTRNumber) {
                    repsDone += 1

                    binding.trainingInProgressExInfo.text =(
                        viewModel.trainingExerciseCrossRef[viewModel.currentExercise].exerciseTRNumber - repsDone).toString()

                }
            }
        }

        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
        //(binding.serialReveicedText.parent as ScrollView).fullScroll(View.FOCUS_DOWN)
    }




}