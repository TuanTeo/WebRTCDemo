package com.example.webrtcdemo.repository

import android.content.Context
import com.example.webrtcdemo.remote.FirebaseClient
import com.example.webrtcdemo.utils.DataModel
import com.example.webrtcdemo.utils.DataModelType
import com.example.webrtcdemo.utils.ErrorCallBack
import com.example.webrtcdemo.utils.NewEventCallBack
import com.example.webrtcdemo.utils.SuccessCallBack
import com.example.webrtcdemo.webrtc.PeerConnectionObserver
import com.example.webrtcdemo.webrtc.WebRtcClient
import com.google.gson.Gson
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer

class MainRepository private constructor() : WebRtcClient.Listener {

    private var firebaseClient: FirebaseClient = FirebaseClient()
    private lateinit var currentUsername: String
    private lateinit var targetUsername: String
    private lateinit var webRtcClient: WebRtcClient
    private lateinit var remoteView: SurfaceViewRenderer

    private lateinit var listener: Listener
    private val gson: Gson = Gson()

    private object Holder {
        val INSTANCE = MainRepository()
    }

    companion object {
        @JvmStatic
        fun getInstance(): MainRepository {
            return Holder.INSTANCE
        }
    }

    fun initLocalView(surfaceViewRenderer: SurfaceViewRenderer) {
        webRtcClient.initLocalSurfaceView(surfaceViewRenderer)
    }

    fun initRemoteView(surfaceViewRenderer: SurfaceViewRenderer) {
        webRtcClient.initRemoteSurfaceView(surfaceViewRenderer)
        this.remoteView = surfaceViewRenderer
    }

    fun startCall(target: String) {
        webRtcClient.call(target)
    }

    fun switchCamera() {
        webRtcClient.switchCamera()
    }

    fun toggleVideo(isMuted: Boolean) {
        webRtcClient.toggleVideo(isMuted)
    }

    fun toggleAudio(isMuted: Boolean) {
        webRtcClient.toggleAudio(isMuted)
    }

    fun endCall() {
        webRtcClient.closeConnection()
    }

    fun login(context: Context, username: String, callBack: SuccessCallBack) {
        firebaseClient.login(username, object : SuccessCallBack {
            override fun onSuccess() {
                currentUsername = username
                webRtcClient = WebRtcClient(
                    context,
                    object: PeerConnectionObserver() {
                        override fun onAddStream(p0: MediaStream?) {
                            super.onAddStream(p0)
                            p0?.videoTracks?.get(0)?.addSink(remoteView)
                        }

                        override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                            super.onConnectionChange(newState)
                            if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
                                listener.onWebRtcConnected()
                            }
                            if (newState == PeerConnection.PeerConnectionState.CLOSED || newState == PeerConnection.PeerConnectionState.DISCONNECTED) {
                                listener.onWebRtcClose()
                            }
                        }

                        override fun onIceCandidate(p0: IceCandidate?) {
                            super.onIceCandidate(p0)
                            p0?.let {
                                webRtcClient.sendIceCandidate(it, targetUsername)
                            }
                        }
                    }, username)
                webRtcClient.setListener(this@MainRepository)
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
                        targetUsername = model.target
                        webRtcClient.onRemoteSessionReceived(
                            SessionDescription(SessionDescription.Type.OFFER, model.data)
                        )
                        webRtcClient.answer(targetUsername)
                    }
                    DataModelType.Answer -> {
                        targetUsername = model.target
                        webRtcClient.onRemoteSessionReceived(
                            SessionDescription(SessionDescription.Type.ANSWER, model.data)
                        )
                    }
                    DataModelType.IceCandidate -> {
                        val candidate = gson.fromJson(model.data, IceCandidate::class.java)
                        webRtcClient.addIceCandidate(candidate)
                    }
                    DataModelType.StartCall -> {
                        targetUsername = model.target
                        callBack.onNewEventReceived(model)
                    }
                }
            }
        })
    }

    override fun onTransferDataToOtherPeer(model: DataModel) {
        firebaseClient.sendMessageToOtherUser(model, object: ErrorCallBack {
            override fun onError() {}
        })
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    interface Listener {
        fun onWebRtcConnected()
        fun onWebRtcClose()
    }
}