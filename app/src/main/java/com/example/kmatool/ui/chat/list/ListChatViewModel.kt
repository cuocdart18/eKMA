package com.example.kmatool.ui.chat.list

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.Data
import com.example.kmatool.common.KEY_MESSAGE_TIMESTAMP_DOC
import com.example.kmatool.common.KEY_ROOMS_COLL
import com.example.kmatool.common.KEY_ROOM_MEMBERS
import com.example.kmatool.common.KEY_ROOM_MESSAGE_COLL
import com.example.kmatool.common.formatMembersToRoomName
import com.example.kmatool.data.models.ChatRoom
import com.example.kmatool.data.models.service.IProfileService
import com.example.kmatool.data.service.ProfileService
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
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
        viewModelScope.launch(Dispatchers.IO) {
            val myStudentCode = profileService.getProfile().studentCode
            roomChangeListener = Data.firestore.collection(KEY_ROOMS_COLL)
                .whereArrayContains(KEY_ROOM_MEMBERS, myStudentCode)
                .addSnapshotListener { value, _ ->
                    if (value != null) {
                        onChatRoomsChanged(value, myStudentCode, callback)
                    }
                }
        }
    }

    private fun onChatRoomsChanged(
        value: QuerySnapshot,
        myStudentCode: String,
        callback: () -> Unit
    ) {
        value.documentChanges.forEach { docChange ->
            if (docChange.type == DocumentChange.Type.ADDED) {
                onDocumentAdded(docChange, myStudentCode, callback)
            }
            if (docChange.type == DocumentChange.Type.MODIFIED) {
                onDocumentModified(docChange, myStudentCode, callback)
            }
            if (docChange.type == DocumentChange.Type.REMOVED) {
                onDocumentRemoved(docChange, myStudentCode, callback)
            }
        }
    }

    private fun onDocumentAdded(
        docChange: DocumentChange,
        myStudentCode: String,
        callback: () -> Unit
    ) {
        val id = docChange.document.id
        val members = docChange.document.get(KEY_ROOM_MEMBERS) as List<String>
        val name = formatMembersToRoomName(members.filter { it != myStudentCode })
        val serverTimestamp = docChange.document.getTimestamp(KEY_MESSAGE_TIMESTAMP_DOC)
        val timestamp = serverTimestamp?.toDate() ?: Date()
        logInfo("ADDED $id")

        docChange.document.reference
            .collection(KEY_ROOM_MESSAGE_COLL)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    return@addOnSuccessListener
                }
                val chatRoom = ChatRoom(id, name, members, timestamp)
                rooms.add(chatRoom)
                rooms.sortByDescending { it.timestamp }
                callback()
            }
    }

    private fun onDocumentModified(
        docChange: DocumentChange,
        myStudentCode: String,
        callback: () -> Unit
    ) {
        val id = docChange.document.id
        val members = docChange.document.get(KEY_ROOM_MEMBERS) as List<String>
        val name = formatMembersToRoomName(members.filter { it != myStudentCode })
        val serverTimestamp = docChange.document.getTimestamp(KEY_MESSAGE_TIMESTAMP_DOC)
        val timestamp = serverTimestamp?.toDate() ?: Date()
        logInfo("MODIFIED $id")

        docChange.document.reference
            .collection(KEY_ROOM_MESSAGE_COLL)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // when a user sends the first message, room is displayed
                if (querySnapshot.size() == 1) {
                    val chatRoom = ChatRoom(id, name, members, timestamp)
                    rooms.add(chatRoom)
                    rooms.sortByDescending { it.timestamp }
                    callback()
                }
                // when a user sends the next message, room is moved to top
                else if (querySnapshot.size() > 1) {
                    rooms.forEach {
                        if (it.id == id) {
                            it.timestamp = timestamp
                            return@forEach
                        }
                    }
                    rooms.sortByDescending { it.timestamp }
                    callback()
                }
            }
    }

    private fun onDocumentRemoved(
        docChange: DocumentChange,
        myStudentCode: String,
        callback: () -> Unit
    ) {
        val id = docChange.document.id
        logError("REMOVED $id")
    }

    override fun onCleared() {
        super.onCleared()
        if (this::roomChangeListener.isInitialized) {
            roomChangeListener.remove()
        }
    }
}