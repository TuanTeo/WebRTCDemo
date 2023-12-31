package com.example.webrtcdemo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.webrtcdemo.R
import com.example.webrtcdemo.databinding.ActivityMainBinding
import com.example.webrtcdemo.repository.MainRepository
import com.example.webrtcdemo.utils.SuccessCallBack
import com.google.firebase.database.FirebaseDatabase
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback

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
            login()
        }
    }

    private fun login() {
        PermissionX.init(this)
            .permissions(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    val username = binding.edtLoginUserName.text.toString()
                    if (username.isNotEmpty()) {
                        // login to firebase
                        MainRepository.getInstance().login(
                            applicationContext,
                            binding.edtLoginUserName.text.toString(),
                            object : SuccessCallBack {
                                override fun onSuccess() {
                                    // go to call activity
                                    startActivity(Intent(this@MainActivity, CallActivity::class.java))
                                }
                            })
                    } else {
                        Toast.makeText(applicationContext, "Please enter username!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}