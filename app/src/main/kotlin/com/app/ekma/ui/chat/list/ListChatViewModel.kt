package com.app.ekma.ui.chat.list

import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.firebase.KEY_ROOMS_COLL
import com.app.ekma.firebase.KEY_ROOM_MEMBERS
import com.app.ekma.firebase.KEY_ROOM_MESSAGE_COLL
import com.app.ekma.common.parseDataToChatRoom
import com.app.ekma.data.models.ChatRoom
import com.app.ekma.data.models.service.IProfileService
import com.app.ekma.firebase.KEY_MESSAGE_SEEN_DOC
import com.app.ekma.firebase.firestore
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
class ListChatViewModel @Inject constructor(
    private val profileService: IProfileService
) : BaseViewModel() {
    override val TAG = ListChatViewModel::class.java.simpleName
    val rooms = mutableListOf<ChatRoom>()
    private lateinit var roomChangeListener: ListenerRegistration

    fun listenChatRoomsChanges(
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val myStudentCode = profileService.getProfile().studentCode
            roomChangeListener = firestore.collection(KEY_ROOMS_COLL)
                .whereArrayContains(KEY_ROOM_MEMBERS, myStudentCode)
                .addSnapshotListener { value, _ ->
                    if (value != null) {
                        onChatRoomsChanged(value, callback)
                    }
                }
        }
    }

    private fun onChatRoomsChanged(
        value: QuerySnapshot,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val myStudentCode = profileService.getProfile().studentCode
            value.documentChanges.forEach { docChange ->
                if (docChange.type == DocumentChange.Type.ADDED) {
                    onRoomAdded(docChange, myStudentCode, callback)
                }
                if (docChange.type == DocumentChange.Type.MODIFIED) {
                    onRoomModified(docChange, myStudentCode, callback)
                }
                if (docChange.type == DocumentChange.Type.REMOVED) {
                    onRoomRemoved(docChange, myStudentCode, callback)
                }
            }
        }
    }

    private suspend fun onRoomAdded(
        docChange: DocumentChange,
        myStudentCode: String,
        callback: () -> Unit
    ) {
        val chatRoom = parseDataToChatRoom(docChange.document, myStudentCode).await()
        val querySnapshot = docChange.document.reference
            .collection(KEY_ROOM_MESSAGE_COLL)
            .get()
            .await()
        if (querySnapshot.isEmpty) {
            return
        }
        rooms.add(chatRoom)
        rooms.sortByDescending { it.timestamp }
        withContext(Dispatchers.Main) {
            callback()
        }
    }

    private suspend fun onRoomModified(
        docChange: DocumentChange,
        myStudentCode: String,
        callback: () -> Unit
    ) {
        val chatRoom = parseDataToChatRoom(docChange.document, myStudentCode).await()
        val querySnapshot = docChange.document.reference
            .collection(KEY_ROOM_MESSAGE_COLL)
            .get()
            .await()
        // when a user sends the first message, room is displayed
        if (querySnapshot.size() == 1) {
            // if room has been exist, don't add
            rooms.forEach {
                if (it.id == chatRoom.id)
                    return
            }
            rooms.add(chatRoom)
            rooms.sortByDescending { it.timestamp }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
        // when a user sends the next message, room is moved to top
        else if (querySnapshot.size() > 1) {
            rooms.forEach { room ->
                if (room.id == chatRoom.id) {
                    room.timestamp = chatRoom.timestamp
                    room.content = chatRoom.content
                    room.from = chatRoom.from
                    room.type = chatRoom.type
                    room.seenMembers = chatRoom.seenMembers
                    return@forEach
                }
            }
            rooms.sortByDescending { it.timestamp }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    private fun onRoomRemoved(
        docChange: DocumentChange,
        myStudentCode: String,
        callback: () -> Unit
    ) {
        val id = docChange.document.id
        logError("REMOVED $id")
    }

    fun modifySeenMembersInRoom(roomId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val myStudentCode = profileService.getProfile().studentCode
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