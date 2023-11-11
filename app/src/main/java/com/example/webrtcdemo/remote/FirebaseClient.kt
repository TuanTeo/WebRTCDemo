package com.example.webrtcdemo.remote

import com.example.webrtcdemo.utils.DataModel
import com.example.webrtcdemo.utils.ErrorCallBack
import com.example.webrtcdemo.utils.NewEventCallBack
import com.example.webrtcdemo.utils.SuccessCallBack
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import java.lang.Exception

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

    fun sendMessageToOtherUser(dataModel: DataModel, errorCallBack: ErrorCallBack) {
        dbRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(dataModel.target).exists()) {
                    dbRef.child(dataModel.target).child(LATEST_EVENT_FIELD_NAME)
                        .setValue(gson.toJson(dataModel))
                } else {
                    errorCallBack.onError()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                errorCallBack.onError()
            }

        })
    }

    fun observeIncomingLatestEvent(newEventCallBack: NewEventCallBack) {
        dbRef.child(currentUserName).child(LATEST_EVENT_FIELD_NAME).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val dataModel = gson.fromJson(snapshot.value.toString(), DataModel::class.java)
                    newEventCallBack.onNewEventReceived(dataModel)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}