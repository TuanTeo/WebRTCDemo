package com.example.webrtcdemo.repository

import com.example.webrtcdemo.remote.FirebaseClient
import com.example.webrtcdemo.utils.SuccessCallBack

class MainRepository private constructor() {

    private var firebaseClient: FirebaseClient = FirebaseClient()
    private lateinit var currentUsername: String

    private object Holder {
        val INSTANCE = MainRepository()
    }

    companion object {
        @JvmStatic
        fun getInstance(): MainRepository {
            return Holder.INSTANCE
        }
    }

    fun login(username: String, callBack: SuccessCallBack) {
        firebaseClient.login(username, object : SuccessCallBack {
            override fun onSuccess() {
                currentUsername = username
                callBack.onSuccess()
            }
        })
    }
}