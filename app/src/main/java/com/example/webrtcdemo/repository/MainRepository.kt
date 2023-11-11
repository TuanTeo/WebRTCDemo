package com.example.webrtcdemo.repository

import com.example.webrtcdemo.remote.FirebaseClient
import com.example.webrtcdemo.utils.DataModel
import com.example.webrtcdemo.utils.DataModelType
import com.example.webrtcdemo.utils.ErrorCallBack
import com.example.webrtcdemo.utils.NewEventCallBack
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

    fun sendCallRequest(target: String, errorCallBack: ErrorCallBack) {
        firebaseClient.sendMessageToOtherUser(
            DataModel(target, currentUsername, null, DataModelType.StartCall),
            errorCallBack
        )
    }

    fun subscribeForLatestEvent(callBack: NewEventCallBack) {
        firebaseClient.observeIncomingLatestEvent(object: NewEventCallBack {
            override fun onNewEventReceived(model: DataModel) {
                when (model.type) {
                    DataModelType.Offer -> {

                    }
                    DataModelType.Answer -> {

                    }
                    DataModelType.IceCandidate -> {

                    }
                    DataModelType.StartCall -> {
                        callBack.onNewEventReceived(model)
                    }
                }
            }
        })
    }
}