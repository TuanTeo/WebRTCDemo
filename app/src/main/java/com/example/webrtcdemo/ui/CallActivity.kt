package com.example.webrtcdemo.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.webrtcdemo.databinding.ActivityCallBinding
import com.example.webrtcdemo.repository.MainRepository
import com.example.webrtcdemo.utils.DataModel
import com.example.webrtcdemo.utils.DataModelType
import com.example.webrtcdemo.utils.ErrorCallBack
import com.example.webrtcdemo.utils.NewEventCallBack

class CallActivity : AppCompatActivity() {

    lateinit var binding: ActivityCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.btnCall.setOnClickListener {
            call()
        }

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
                    }
                }
            }
        })
    }

    private fun call() {
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
}