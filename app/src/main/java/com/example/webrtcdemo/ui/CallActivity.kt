package com.example.webrtcdemo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.webrtcdemo.databinding.ActivityCallBinding

class CallActivity : AppCompatActivity() {

    lateinit var binding: ActivityCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}