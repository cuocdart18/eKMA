package com.example.kmatool.ui.chat.main

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.Data
import com.example.kmatool.common.IMAGE_MSG
import com.example.kmatool.common.KEY_MESSAGE_CONTENT_DOC
import com.example.kmatool.common.KEY_MESSAGE_FROM_DOC
import com.example.kmatool.common.KEY_MESSAGE_TIMESTAMP_DOC
import com.example.kmatool.common.KEY_MESSAGE_TYPE_DOC
import com.example.kmatool.common.KEY_ROOMS_COLL
import com.example.kmatool.common.KEY_ROOM_MESSAGE_COLL
import com.example.kmatool.common.ROOMS_DIR
import com.example.kmatool.common.TEXT_MSG
import com.example.kmatool.common.TedImagePickerStarter
import com.example.kmatool.data.models.Message
import com.example.kmatool.firebase.firestore
import com.example.kmatool.firebase.storage
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class ChatViewModel @Inject constructor() : BaseViewModel() {
    override val TAG = ChatViewModel::class.java.simpleName
    private lateinit var msgCollRef: CollectionReference
    private lateinit var msgChangeRegis: ListenerRegistration
    private lateinit var lastVisibleDoc: DocumentSnapshot

    var roomId = ""
    var isLoading = false
    var isLastPage = false

    val messages = mutableListOf<Message>()
    private val messagePerPage = 30L
    var totalPage = 0
    var currentPage = 1

    fun sendImageFromPicker(context: Context) {
        TedImagePickerStarter.pickMultiImageForChatting(context) { uris ->
            GlobalScope.launch(Dispatchers.IO) {
                val imgName = Date().time
                val imageRef = storage.child("$ROOMS_DIR/$roomId/$imgName")
                imageRef.putFile(uris.first()).addOnSuccessListener {
                    imageRef.downloadUrl.addOnCompleteListener { task ->
                        if (!task.isSuccessful) return@addOnCompleteListener
                        val url = task.result.toString()
                        sendMessage(url, IMAGE_MSG)
                    }.addOnFailureListener {
                        it as StorageException
                        logError(it.toString())
                    }
                }.addOnFailureListener {
                    it as StorageException
                    logError(it.toString())
                }
            }
        }
    }

    fun sendMessage(content: String, type: Int) {
        val myStudentCode = Data.profile.studentCode
        val messageMap = mapOf(
            KEY_MESSAGE_TIMESTAMP_DOC to FieldValue.serverTimestamp(),
            KEY_MESSAGE_CONTENT_DOC to content,
            KEY_MESSAGE_FROM_DOC to myStudentCode,
            KEY_MESSAGE_TYPE_DOC to type
        )
        val docRoomRef = firestore
            .collection(KEY_ROOMS_COLL)
            .document(roomId)
        docRoomRef
            .collection(KEY_ROOM_MESSAGE_COLL)
            .add(messageMap)
            .addOnSuccessListener {
                docRoomRef.update(messageMap)
            }
    }

    fun getTotalMessageCount(
        callback: () -> Unit
    ) {
        // init collection reference
        msgCollRef = firestore
            .collection(KEY_ROOMS_COLL)
            .document(roomId)
            .collection(KEY_ROOM_MESSAGE_COLL)

        msgCollRef.count()
            .get(AggregateSource.SERVER)
            .addOnSuccessListener { task ->
                val totalMessage = task.count
                totalPage = ceil(totalMessage.toDouble() / messagePerPage).toInt()
                logInfo("totalPage=$totalPage")
                callback()
            }
    }

    fun observeMessageDocChanges(addEleCallback: (itemCount: Int) -> Unit) {
        var isFirstGetMessage = true
        msgChangeRegis = msgCollRef
            .orderBy(KEY_MESSAGE_TIMESTAMP_DOC, Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                if (isFirstGetMessage) {
                    isFirstGetMessage = false
                    return@addSnapshotListener
                }
                viewModelScope.launch(Dispatchers.IO) {
                    if (value == null) return@launch
                    listenMessagesAdded(value, addEleCallback)
                }
            }
    }

    private suspend fun listenMessagesAdded(
        value: QuerySnapshot,
        addEleCallback: (itemCount: Int) -> Unit
    ) {
        var itemCount = 0
        value.documentChanges
            .forEach {
                if (it.type == DocumentChange.Type.ADDED) {
                    val serverTimestamp = it.document.getTimestamp(KEY_MESSAGE_TIMESTAMP_DOC)
                    val timestamp = serverTimestamp?.toDate() ?: Date()
                    val content = it.document.get(KEY_MESSAGE_CONTENT_DOC).toString()
                    val from = it.document.get(KEY_MESSAGE_FROM_DOC).toString()
                    val type = it.document.getLong(KEY_MESSAGE_TYPE_DOC)?.toInt() ?: TEXT_MSG
                    val message = Message(timestamp, content, from, type)
                    logInfo("add $message")
                    messages.add(message)
                    itemCount++
                }
            }
        logError("size=${messages.size}")
        withContext(Dispatchers.Main) {
            addEleCallback(itemCount)
        }
    }

    fun getOlderMessage(addEleCallback: (itemCount: Int) -> Unit) {
        val messagesTemp = mutableListOf<Message>()
        if (this::lastVisibleDoc.isInitialized) {
            msgCollRef
                .orderBy(KEY_MESSAGE_TIMESTAMP_DOC, Query.Direction.DESCENDING)
                .startAfter(lastVisibleDoc)
                .limit(messagePerPage)
                .get()
                .addOnSuccessListener { query ->
                    getMsgPerPage(query, messagesTemp)
                    addEleCallback(messagesTemp.size)
                }
        } else {
            msgCollRef
                .orderBy(KEY_MESSAGE_TIMESTAMP_DOC, Query.Direction.DESCENDING)
                .limit(messagePerPage)
                .get()
                .addOnSuccessListener { query ->
                    getMsgPerPage(query, messagesTemp)
                    addEleCallback(messagesTemp.size)
                }
        }
    }

    private fun getMsgPerPage(query: QuerySnapshot, messagesTemp: MutableList<Message>) {
        query.documents.forEach { doc ->
            val serverTimestamp = doc.getTimestamp(KEY_MESSAGE_TIMESTAMP_DOC)
            val timestamp = serverTimestamp?.toDate() ?: Date()
            val content = doc.get(KEY_MESSAGE_CONTENT_DOC).toString()
            val from = doc.get(KEY_MESSAGE_FROM_DOC).toString()
            val type = doc.getLong(KEY_MESSAGE_TYPE_DOC)?.toInt() ?: TEXT_MSG
            val message = Message(timestamp, content, from, type)
            messagesTemp.add(message)
        }
        if (query.documents.isNotEmpty()) {
            lastVisibleDoc = query.documents.last()
        }
        messagesTemp.reverse()
        messages.addAll(0, messagesTemp)
        logError("size=${messages.size}")
    }

    override fun onCleared() {
        super.onCleared()
        if (this::msgChangeRegis.isInitialized) {
            msgChangeRegis.remove()
        }
    }
}