package com.example.webrtcdemo.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.webrtcdemo.R
import com.example.webrtcdemo.databinding.ActivityCallBinding
import com.example.webrtcdemo.repository.MainRepository
import com.example.webrtcdemo.utils.DataModel
import com.example.webrtcdemo.utils.DataModelType
import com.example.webrtcdemo.utils.ErrorCallBack
import com.example.webrtcdemo.utils.NewEventCallBack

class CallActivity : AppCompatActivity(), MainRepository.Listener {

    private var isBackCamera: Boolean = false
    lateinit var binding: ActivityCallBinding
    private var isMutedAudio = false
    private var isMutedVideo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.btnCall.setOnClickListener {
            startCall()
        }

        MainRepository.getInstance().initLocalView(binding.svrLocalView)
        MainRepository.getInstance().initRemoteView(binding.svrRemoteView)
        MainRepository.getInstance().setListener(this)
        MainRepository.getInstance().subscribeForLatestEvent(object: NewEventCallBack {
            override fun onNewEventReceived(model: DataModel) {
                if (model.type == DataModelType.StartCall) {
                    binding.rlIncomingCall.visibility = View.VISIBLE
                    binding.tvIncomingUsername.text = "${model.sender} is calling..."
                    binding.ibtnEndCall.setOnClickListener {
                        binding.rlIncomingCall.visibility = View.GONE
                    }
                    binding.ibtnStartCall.setOnClickListener {
                        // Start the call
                        binding.groupCallRequest.visibility = View.GONE
                        binding.rlIncomingCall.visibility = View.GONE
                        binding.groupCallView.visibility = View.VISIBLE
                        MainRepository.getInstance().startCall(model.sender)
                    }
                }
            }
        })

        binding.btnSwitchCamera.setOnClickListener {
            MainRepository.getInstance().switchCamera()
            binding.svrLocalView.setMirror(isBackCamera)
            isBackCamera = !isBackCamera
        }
        binding.btnMic.setOnClickListener {
            isMutedAudio = !isMutedAudio
            binding.btnMic.setImageResource(if (isMutedAudio) R.drawable.baseline_mic_off_24 else R.drawable.baseline_mic_none_24)
            MainRepository.getInstance().toggleAudio(isMutedAudio)
        }
        binding.btnVideo.setOnClickListener {
            isMutedVideo = !isMutedVideo
            binding.btnVideo.setImageResource(if (isMutedVideo) R.drawable.baseline_videocam_off_24 else R.drawable.baseline_videocam_24)
            MainRepository.getInstance().toggleVideo(isMutedVideo)
        }

        binding.btnEndCall.setOnClickListener {
            MainRepository.getInstance().endCall()
            finish()
        }
    }

    private fun startCall() {
        val targetUsername = binding.edtTargetUser.text.toString()
        if (targetUsername.isNotEmpty()) {
            // Start a call request
            MainRepository.getInstance().sendCallRequest(targetUsername, object: ErrorCallBack {
                override fun onError() {
                    Toast.makeText(applicationContext, "Couldn't find the target user!", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(applicationContext, "Please enter username!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onWebRtcConnected() {
        runOnUiThread {
            binding.groupCallRequest.visibility = View.GONE
            binding.rlIncomingCall.visibility = View.GONE
            binding.groupCallView.visibility = View.VISIBLE
        }
    }

    override fun onWebRtcClose() {
        finish()
    }
}