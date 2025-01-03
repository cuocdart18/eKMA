package com.app.ekma.ui.chat.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.IMAGE_MSG
import com.app.ekma.common.TedImagePickerStarter
import com.app.ekma.common.jsonObjectToString
import com.app.ekma.common.parseDataToMessage
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.common.removeStudentCode
import com.app.ekma.data.models.Message
import com.app.ekma.data.models.service.IFcmService
import com.app.ekma.firebase.ACTIVE_STATUS
import com.app.ekma.firebase.AVATAR_FILE
import com.app.ekma.firebase.CONNECTIONS
import com.app.ekma.firebase.KEY_MESSAGE_CONTENT_DOC
import com.app.ekma.firebase.KEY_MESSAGE_FROM_DOC
import com.app.ekma.firebase.KEY_MESSAGE_SEEN_DOC
import com.app.ekma.firebase.KEY_MESSAGE_TIMESTAMP_DOC
import com.app.ekma.firebase.KEY_MESSAGE_TYPE_DOC
import com.app.ekma.firebase.KEY_ROOMS_COLL
import com.app.ekma.firebase.KEY_ROOM_MEMBERS
import com.app.ekma.firebase.KEY_ROOM_MESSAGE_COLL
import com.app.ekma.firebase.KEY_USERS_COLL
import com.app.ekma.firebase.KEY_USER_NAME
import com.app.ekma.firebase.ROOMS_DIR
import com.app.ekma.firebase.USERS_DIR
import com.app.ekma.firebase.database
import com.app.ekma.firebase.firestore
import com.app.ekma.firebase.storage
import com.app.ekma.work.WorkRunner
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val fcmService: IFcmService
) : BaseViewModel() {
    override val TAG = ChatViewModel::class.java.simpleName

    private lateinit var msgCollRef: CollectionReference
    private lateinit var msgChangeRegis: ListenerRegistration
    private lateinit var lastVisibleMsgPerPage: DocumentSnapshot

    private lateinit var membersCode: MutableList<String>
    var roomId = ""

    private val _showScrollPopUp = MutableStateFlow(false)
    val showScrollPopUp: StateFlow<Boolean>
        get() = _showScrollPopUp.asStateFlow()

    private val _enableSendMsg = MutableStateFlow(false)
    val enableSendMsg: StateFlow<Boolean>
        get() = _enableSendMsg.asStateFlow()

    private val _activeStatus = MutableStateFlow(false)
    val activeStatus: StateFlow<Boolean>
        get() = _activeStatus.asStateFlow()

    private val _roomName = MutableStateFlow("Friend")
    val roomName: StateFlow<String>
        get() = _roomName.asStateFlow()

    private val _roomImage = MutableStateFlow<Uri?>(null)
    val roomImage: StateFlow<Uri?>
        get() = _roomImage.asStateFlow()

    val messages = mutableListOf<Message>()
    private val messagePerPage = 30L
    var totalPage = 0
    var currentPage = 1
    var isLoading = false
    var isLastPage = false

    private val _modifiedMsgPosition = MutableStateFlow(-1)
    val modifiedMsgPosition: StateFlow<Int>
        get() = _modifiedMsgPosition.asStateFlow()
    private var lastMsgPosition = -1

    fun setShowScrollPopUp(hasShow: Boolean) {
        viewModelScope.launch {
            _showScrollPopUp.value = hasShow
        }
    }

    fun setEnableBtnSendMsg(hasEnable: Boolean) {
        viewModelScope.launch {
            _enableSendMsg.value = hasEnable
        }
    }

    fun getChatRoomNameAndImage() {
        viewModelScope.launch {
            firestore.collection(KEY_ROOMS_COLL)
                .document(roomId)
                .get()
                .addOnSuccessListener { snap ->
                    val membersCode = snap.get(KEY_ROOM_MEMBERS) as List<String>
                    val friendCode =
                        removeStudentCode(membersCode, ProfileSingleton().studentCode).first()
                    firestore.collection(KEY_USERS_COLL)
                        .document(friendCode)
                        .get()
                        .addOnSuccessListener { snap ->
                            _roomName.value = snap.getString(KEY_USER_NAME) ?: "Friend"
                        }
                    storage.child("$USERS_DIR/$friendCode/$AVATAR_FILE")
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            _roomImage.value = uri
                        }.addOnFailureListener {
                            _roomImage.value = null
                        }
                }
        }
    }

    fun getMembersCode(
        callback: () -> Unit
    ) {
        if (this::membersCode.isInitialized) {
            callback()
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                membersCode = mutableListOf()
                membersCode.addAll(
                    firestore.collection(KEY_ROOMS_COLL)
                        .document(roomId)
                        .get()
                        .await()
                        .get(KEY_ROOM_MEMBERS) as List<String>
                )
                withContext(Dispatchers.Main) {
                    callback()
                }
            }
        }
    }

    fun regisActiveStatusChange() {
        viewModelScope.launch(Dispatchers.IO) {
            removeStudentCode(membersCode, ProfileSingleton().studentCode).forEach { studentCode ->
                launch {
                    database.child(ACTIVE_STATUS)
                        .child(studentCode)
                        .child(CONNECTIONS)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                viewModelScope.launch(Dispatchers.Default) {
                                    val data = snapshot.value
                                    var isOnline = false
                                    if (data != null) {
                                        data as Map<String, Boolean>
                                        isOnline = data.values.contains(true)
                                    }
                                    withContext(Dispatchers.Main) {
                                        _activeStatus.value = isOnline
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                logWarning("onCancelled: ${error.toException()}")
                            }
                        })
                }
            }
        }
    }

    fun sendMessage(context: Context, content: String, type: Int) {
        val message = content.trim()
        if (message.isEmpty()) return

        val timeStamp = FieldValue.serverTimestamp()
        val myCode = ProfileSingleton().studentCode
        val messageMap = mapOf(
            KEY_MESSAGE_TIMESTAMP_DOC to timeStamp,
            KEY_MESSAGE_CONTENT_DOC to message,
            KEY_MESSAGE_FROM_DOC to myCode,
            KEY_MESSAGE_TYPE_DOC to type,
            KEY_MESSAGE_SEEN_DOC to listOf(myCode)
        )
        val docRoomRef = firestore
            .collection(KEY_ROOMS_COLL)
            .document(roomId)
        docRoomRef
            .collection(KEY_ROOM_MESSAGE_COLL)
            .add(messageMap)
            .addOnSuccessListener {
                docRoomRef.update(messageMap)

                WorkRunner.runNotifyNewMsgWorker(
                    workManager = WorkManager.getInstance(context),
                    receiverCodes = membersCode.filter { it != myCode },
                    msgData = jsonObjectToString(messageMap)
                )
            }
    }

    fun sendImageFromPicker(context: Context) {
        TedImagePickerStarter.pickMultiImageForChatting(context) { uris ->
            GlobalScope.launch(Dispatchers.IO) {
                val imgName = Date().time
                val imageRef = storage.child("$ROOMS_DIR/$roomId/$imgName")
                imageRef.putFile(uris.first()).addOnSuccessListener {
                    imageRef.downloadUrl.addOnCompleteListener { task ->
                        if (!task.isSuccessful) return@addOnCompleteListener
                        val url = task.result.toString()
                        sendMessage(
                            context = context,
                            content = url,
                            type = IMAGE_MSG
                        )
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
                callback()
            }
    }

    fun observeMessageChanges(
        callback: (Int) -> Unit
    ) {
        var isFirstGetMessage = true
        msgChangeRegis = msgCollRef
            .orderBy(KEY_MESSAGE_TIMESTAMP_DOC, Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                if (isFirstGetMessage) {
                    isFirstGetMessage = false
                    return@addSnapshotListener
                }
                viewModelScope.launch(Dispatchers.IO) {
                    value?.documentChanges?.forEach { docChange ->
                        if (docChange.type == DocumentChange.Type.ADDED) {
                            onMessageAdded(docChange, callback)
                        }
                        if (docChange.type == DocumentChange.Type.MODIFIED) {
                            onMessageModified(docChange, callback)
                        }
                        if (docChange.type == DocumentChange.Type.REMOVED) {
                            onMessageRemoved(docChange, callback)
                        }
                    }
                }
            }
    }

    private suspend fun onMessageAdded(
        docChange: DocumentChange,
        callback: (Int) -> Unit
    ) {
        val message = parseDataToMessage(docChange.document)
        viewModelScope.launch {
            val seen = message.seen
            if (!seen.contains(ProfileSingleton().studentCode)) {
                seen.add(ProfileSingleton().studentCode)
                // update seen members of message
                docChange.document.reference.update(mapOf(KEY_MESSAGE_SEEN_DOC to seen))
                // update seen members of room
                delay(1500) // avoid conflict transaction in 1s
                firestore
                    .collection(KEY_ROOMS_COLL)
                    .document(roomId)
                    .update(mapOf(KEY_MESSAGE_SEEN_DOC to seen))
            }
        }
        // add to list message item
        messages.add(message)
        logInfo("ADDED $message")

        withContext(Dispatchers.Main) {
            callback(1)
        }
    }

    private suspend fun onMessageModified(
        docChange: DocumentChange,
        callback: (Int) -> Unit
    ) {
        val message = parseDataToMessage(docChange.document)
        messages.forEach {
            if (it.id == message.id) {
                it.seen = message.seen
                return@forEach
            }
        }
        findTheLastSeenMessage()
    }

    private suspend fun onMessageRemoved(
        docChange: DocumentChange,
        callback: (Int) -> Unit
    ) {

    }

    fun getOlderMessage(addEleCallback: (itemCount: Int) -> Unit) {
        val messagesTemp = mutableListOf<Message>()
        if (this::lastVisibleMsgPerPage.isInitialized) {
            msgCollRef
                .orderBy(KEY_MESSAGE_TIMESTAMP_DOC, Query.Direction.DESCENDING)
                .startAfter(lastVisibleMsgPerPage)
                .limit(messagePerPage)
                .get()
                .addOnSuccessListener { query ->
                    getMsgPerPage(query, messagesTemp)
                    addEleCallback(messagesTemp.size)
                    findTheLastSeenMessage()
                }
        } else {
            msgCollRef
                .orderBy(KEY_MESSAGE_TIMESTAMP_DOC, Query.Direction.DESCENDING)
                .limit(messagePerPage)
                .get()
                .addOnSuccessListener { query ->
                    getMsgPerPage(query, messagesTemp)
                    addEleCallback(messagesTemp.size)
                    findTheLastSeenMessage()
                }
        }
    }

    private fun getMsgPerPage(query: QuerySnapshot, messagesTemp: MutableList<Message>) {
        query.documents.forEach { doc ->
            // update seen members
            viewModelScope.launch {
                val seen = doc.get(KEY_MESSAGE_SEEN_DOC) as MutableList<String>
                if (!seen.contains(ProfileSingleton().studentCode)) {
                    seen.add(ProfileSingleton().studentCode)
                    doc.reference.update(mapOf(KEY_MESSAGE_SEEN_DOC to seen))
                }
            }
            // add to list message item
            val message = parseDataToMessage(doc)
            messagesTemp.add(message)
        }
        if (query.documents.isNotEmpty()) {
            lastVisibleMsgPerPage = query.documents.last()
        }
        messagesTemp.reverse()
        messages.addAll(0, messagesTemp)
    }

    private fun findTheLastSeenMessage() {
        viewModelScope.launch(Dispatchers.Default) {
            if (lastMsgPosition != -1) {
                messages[lastMsgPosition].isLastSeenMessage = false
            }
            for (i in messages.size - 1 downTo 0) {
                if (messages[i].seen.containsAll(membersCode) and (messages[i].from == ProfileSingleton().studentCode)) {
                    messages[i].isLastSeenMessage = true
                    withContext(Dispatchers.Main) {
                        _modifiedMsgPosition.value = lastMsgPosition
                        lastMsgPosition = i
                    }
                    break
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this::msgChangeRegis.isInitialized) {
            msgChangeRegis.remove()
        }
    }
}