package com.example.webrtcdemo.remote

import com.example.webrtcdemo.utils.SuccessCallBack
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson

class FirebaseClient {

    companion object {
        const val LATEST_EVENT_FIELD_NAME = "latest_event"
    }

    private val gson: Gson = Gson()
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var currentUserName: String

    fun login(userName: String, callBack: SuccessCallBack) {
        dbRef.child(userName).setValue("").addOnCompleteListener {
            currentUserName = userName
            callBack.onSuccess()
        }
    }

    fun sendMessageToOtherUser() {

    }

    fun observeIncomingLatestEvent() {

    }
}