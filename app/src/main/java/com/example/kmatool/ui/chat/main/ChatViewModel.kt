package com.example.kmatool.ui.chat.main

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.Data
import com.example.kmatool.common.KEY_MESSAGE_CONTENT_DOC
import com.example.kmatool.common.KEY_MESSAGE_FROM_DOC
import com.example.kmatool.common.KEY_MESSAGE_TIMESTAMP_DOC
import com.example.kmatool.common.KEY_ROOMS_COLL
import com.example.kmatool.common.KEY_ROOM_MESSAGE_COLL
import com.example.kmatool.data.models.Message
import com.example.kmatool.data.models.service.IProfileService
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val profileService: IProfileService
) : BaseViewModel() {
    override val TAG = ChatViewModel::class.java.simpleName
    var roomId = ""
    val messages = mutableListOf<Message>()
    private lateinit var msgChangeRegistration: ListenerRegistration

    fun sendMessage(content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val myStudentCode = profileService.getProfile().studentCode
            val messageMap = mapOf(
                KEY_MESSAGE_TIMESTAMP_DOC to FieldValue.serverTimestamp(),
                KEY_MESSAGE_CONTENT_DOC to content,
                KEY_MESSAGE_FROM_DOC to myStudentCode
            )
            val docRoomRef = Data.firestore
                .collection(KEY_ROOMS_COLL)
                .document(roomId)

            docRoomRef
                .collection(KEY_ROOM_MESSAGE_COLL)
                .add(messageMap)
                .addOnSuccessListener {
                    docRoomRef.update(messageMap)
                }
        }
    }

    fun listenMessageDocChanges(
        firstAddEleCallback: () -> Unit,
        notFirstAddEleCallback: (itemCount: Int) -> Unit
    ) {
        msgChangeRegistration = Data.firestore
            .collection(KEY_ROOMS_COLL)
            .document(roomId)
            .collection(KEY_ROOM_MESSAGE_COLL)
            .orderBy(KEY_MESSAGE_TIMESTAMP_DOC, Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                viewModelScope.launch(Dispatchers.IO) {
                    if (value != null) {
                        listenMessagesAdded(value, firstAddEleCallback, notFirstAddEleCallback)
                    }
                }
            }
    }

    private suspend fun listenMessagesAdded(
        value: QuerySnapshot,
        firstAddEleCallback: () -> Unit,
        notFirstAddEleCallback: (itemCount: Int) -> Unit
    ) {
        val count = messages.size
        value.documentChanges.forEach {
            if (it.type == DocumentChange.Type.ADDED) {
                val serverTimestamp = it.document.getTimestamp(KEY_MESSAGE_TIMESTAMP_DOC)
                val timestamp = serverTimestamp?.toDate() ?: Date()
                val content = it.document.get(KEY_MESSAGE_CONTENT_DOC).toString()
                val from = it.document.get(KEY_MESSAGE_FROM_DOC).toString()
                val message = Message(timestamp, content, from)
                logInfo(message.toString())
                messages.add(message)
            }
        }
        withContext(Dispatchers.Main) {
            if (count == 0) {
                firstAddEleCallback()
            } else {
                notFirstAddEleCallback(messages.size - count)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this::msgChangeRegistration.isInitialized) {
            msgChangeRegistration.remove()
        }
    }
}