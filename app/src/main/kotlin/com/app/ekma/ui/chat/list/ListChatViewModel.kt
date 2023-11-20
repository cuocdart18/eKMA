package com.app.ekma.ui.chat.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.FROM_POSITION
import com.app.ekma.common.TO_POSITION
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.firebase.KEY_ROOMS_COLL
import com.app.ekma.firebase.KEY_ROOM_MEMBERS
import com.app.ekma.firebase.KEY_ROOM_MESSAGE_COLL
import com.app.ekma.common.parseDataToChatRoom
import com.app.ekma.common.removeStudentCode
import com.app.ekma.data.models.ChatRoom
import com.app.ekma.firebase.ACTIVE_STATUS
import com.app.ekma.firebase.CONNECTIONS
import com.app.ekma.firebase.KEY_MESSAGE_SEEN_DOC
import com.app.ekma.firebase.database
import com.app.ekma.firebase.firestore
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListChatViewModel @Inject constructor() : BaseViewModel() {
    override val TAG = ListChatViewModel::class.java.simpleName
    val rooms = mutableListOf<ChatRoom>()
    private lateinit var roomChangeListener: ListenerRegistration

    private val _addedRoomPos = MutableLiveData(-1)
    val addedRoomPos: LiveData<Int>
        get() = _addedRoomPos

    private val _movedRoomPos = MutableLiveData(
        mapOf(
            FROM_POSITION to -1,
            TO_POSITION to -1
        )
    )
    val movedRoomPos: LiveData<Map<String, Int>>
        get() = _movedRoomPos

    private val _modifiedRoomPos = MutableLiveData(-1)
    val modifiedRoomPos: LiveData<Int>
        get() = _modifiedRoomPos

    fun listenChatRoomsChanges() {
        viewModelScope.launch {
            roomChangeListener = firestore.collection(KEY_ROOMS_COLL)
                .whereArrayContains(KEY_ROOM_MEMBERS, ProfileSingleton().studentCode)
                .addSnapshotListener { value, _ ->
                    if (value != null) {
                        onChatRoomsChanged(value)
                    }
                }
        }
    }

    private fun onChatRoomsChanged(value: QuerySnapshot) {
        viewModelScope.launch(Dispatchers.IO) {
            value.documentChanges.forEach { docChange ->
                if (docChange.type == DocumentChange.Type.ADDED) {
                    onRoomAdded(docChange)
                }
                if (docChange.type == DocumentChange.Type.MODIFIED) {
                    onRoomModified(docChange)
                }
                if (docChange.type == DocumentChange.Type.REMOVED) {
                    onRoomRemoved(docChange)
                }
            }
        }
    }

    private suspend fun onRoomAdded(docChange: DocumentChange) {
        val chatRoom =
            parseDataToChatRoom(docChange.document, ProfileSingleton().studentCode).await()
        val querySnapshot = docChange.document.reference
            .collection(KEY_ROOM_MESSAGE_COLL)
            .get()
            .await()
        if (querySnapshot.isEmpty) {
            return
        }
        rooms.add(0, chatRoom)
        setActiveStatus(chatRoom)
        withContext(Dispatchers.Main) {
            _addedRoomPos.value = 0
        }
    }

    private suspend fun onRoomModified(docChange: DocumentChange) {
        val chatRoom =
            parseDataToChatRoom(docChange.document, ProfileSingleton().studentCode).await()
        val querySnapshot = docChange.document.reference
            .collection(KEY_ROOM_MESSAGE_COLL)
            .get()
            .await()
        // when a user sends the first message, room is displayed
        if (querySnapshot.size() == 1) {
            // if room has been exist, don't add
            rooms.forEach {
                if (it.id == chatRoom.id) return
            }
            rooms.add(0, chatRoom)
            setActiveStatus(chatRoom)
            withContext(Dispatchers.Main) {
                _addedRoomPos.value = 0
            }
        }
        // when a user sends the next message, room is moved to top
        else if (querySnapshot.size() > 1) {
            var from = -1
            val to = 0
            for (i in rooms.indices) {
                if (rooms[i].id == chatRoom.id) {
                    chatRoom.isOnline = rooms[i].isOnline
                    from = i
                    rooms.removeAt(from)
                    rooms.add(to, chatRoom)
                    break
                }
            }
            withContext(Dispatchers.Main) {
                _movedRoomPos.value = mapOf(
                    FROM_POSITION to from,
                    TO_POSITION to to
                )
            }
        }
    }

    private fun onRoomRemoved(docChange: DocumentChange) {
        val id = docChange.document.id
        logError("REMOVED $id")
    }

    private fun setActiveStatus(chatRoom: ChatRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            removeStudentCode(
                chatRoom.members,
                ProfileSingleton().studentCode
            ).forEach { studentCode ->
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
                                    for (i in rooms.indices) {
                                        if (rooms[i].id == chatRoom.id) {
                                            rooms[i].isOnline = isOnline
                                            withContext(Dispatchers.Main) {
                                                _modifiedRoomPos.value = i
                                            }
                                            break
                                        }
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

    fun modifySeenMembersInRoom(roomId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val myStudentCode = ProfileSingleton().studentCode
            rooms.forEach { room ->
                if (room.id == roomId && !room.seenMembers.contains(myStudentCode)) {
                    // modify seen members in room list
                    room.seenMembers.add(myStudentCode)
                    // modify seen members of room in firebase
                    firestore.collection(KEY_ROOMS_COLL)
                        .document(roomId)
                        .update(
                            mapOf(KEY_MESSAGE_SEEN_DOC to room.seenMembers)
                        )
                    return@forEach
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this::roomChangeListener.isInitialized) {
            roomChangeListener.remove()
        }
    }
}