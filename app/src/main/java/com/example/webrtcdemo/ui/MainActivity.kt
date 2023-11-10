package com.example.webrtcdemo.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.webrtcdemo.R
import com.example.webrtcdemo.databinding.ActivityMainBinding
import com.example.webrtcdemo.repository.MainRepository
import com.example.webrtcdemo.utils.SuccessCallBack
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.btnLogin.setOnClickListener {
            // login to firebase
            MainRepository.getInstance().login(
                binding.edtLoginUserName.text.toString(),
                object : SuccessCallBack {
                    override fun onSuccess() {
                        // go to call activity
                        startActivity(Intent(this@MainActivity, CallActivity::class.java))
                    }
                })
        }

    }
}