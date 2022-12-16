package hu.kristof.nagy.sidp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.util.*

class AudioRecorderFragment : Fragment() {
    companion object {
        const val TAG = "AudioRecorderFragment"
    }

    lateinit var recorder: MediaRecorder
    lateinit var player: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_recorder, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED) {
            val date = Calendar.getInstance().time.toString()
            val file = File(activity?.filesDir, "$date.3gp")

            prepareMediaRecorder(file, requireContext())
            prepareMediaPlayer(Uri.fromFile(file))

            val recordButton = view.findViewById<ToggleButton>(R.id.record_btn)
            recordButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    recorder.start()
                } else {
                    recorder.stop()
                }
            }

            val playButton = view.findViewById<Button>(R.id.playback_btn)
            playButton.setOnClickListener { btn ->
                val button = btn as Button
                when (button.text) {
                    "Play" -> {
                        player.start()
                        button.text = "Stop"
                    }
                    "Stop" -> {
                        player.stop()
                        button.text = "Play"
                    }
                }
            }

            player.setOnCompletionListener {
                playButton.text = "Play"
            }

        } else {
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    Toast.makeText(context, "Audio recording is unavailable.", Toast.LENGTH_SHORT).show()
                }
            }.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun prepareMediaRecorder(file: File, context: Context) {
        recorder = MediaRecorder(context).apply {
            // raw audio: https://developer.android.com/guide/topics/media/mediarecorder
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(file)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e(TAG, "prepare() failed with message: ${e.message}")
            }
        }
    }

    fun prepareMediaPlayer(uri: Uri) {
        player = MediaPlayer().apply {
            try {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(requireContext(), uri)
                prepareAsync()
            } catch (e: IOException) {
                Log.e(TAG, "MediaPlayer() failed with message: ${e.message}")
            }
        }
    }
}