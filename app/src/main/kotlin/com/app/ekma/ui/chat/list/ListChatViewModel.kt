package com.app.ekma.ui.chat.list

import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.KEY_MESSAGE_CONTENT_DOC
import com.app.ekma.common.KEY_MESSAGE_FROM_DOC
import com.app.ekma.common.KEY_MESSAGE_TIMESTAMP_DOC
import com.app.ekma.common.KEY_MESSAGE_TYPE_DOC
import com.app.ekma.common.KEY_ROOMS_COLL
import com.app.ekma.common.KEY_ROOM_MEMBERS
import com.app.ekma.common.KEY_ROOM_MESSAGE_COLL
import com.app.ekma.common.formatMembersToRoomName
import com.app.ekma.common.removeMyStudentCode
import com.app.ekma.data.models.ChatRoom
import com.app.ekma.data.models.service.IProfileService
import com.app.ekma.firebase.firestore
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
    }

    private suspend fun onDocumentAdded(
        docChange: DocumentChange,
        myStudentCode: String,
        callback: () -> Unit
    ) {
        val id = docChange.document.id
        val members = docChange.document.get(KEY_ROOM_MEMBERS) as List<String>
        val name = formatMembersToRoomName(removeMyStudentCode(members, myStudentCode))
        val serverTimestamp = docChange.document.getTimestamp(KEY_MESSAGE_TIMESTAMP_DOC)
        val timestamp = serverTimestamp?.toDate() ?: Date()
        val content = docChange.document.get(KEY_MESSAGE_CONTENT_DOC).toString()
        val from = docChange.document.get(KEY_MESSAGE_FROM_DOC).toString()
        val type = (docChange.document.getLong(KEY_MESSAGE_TYPE_DOC) ?: 1).toInt()
        logInfo("ADDED $id")

        docChange.document.reference
            .collection(KEY_ROOM_MESSAGE_COLL)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    return@addOnSuccessListener
                }
                val chatRoom = ChatRoom(id, name, members, timestamp, content, from, type)
                rooms.add(chatRoom)
                rooms.sortByDescending { it.timestamp }
                callback()
            }
    }

    private suspend fun onDocumentModified(
        docChange: DocumentChange,
        myStudentCode: String,
        callback: () -> Unit
    ) {
        val id = docChange.document.id
        val members = docChange.document.get(KEY_ROOM_MEMBERS) as List<String>
        val name = formatMembersToRoomName(removeMyStudentCode(members, myStudentCode))
        val serverTimestamp = docChange.document.getTimestamp(KEY_MESSAGE_TIMESTAMP_DOC)
        val timestamp = serverTimestamp?.toDate() ?: Date()
        val content = docChange.document.get(KEY_MESSAGE_CONTENT_DOC).toString()
        val from = docChange.document.get(KEY_MESSAGE_FROM_DOC).toString()
        val type = (docChange.document.getLong(KEY_MESSAGE_TYPE_DOC) ?: 1).toInt()
        logInfo("MODIFIED $id")

        docChange.document.reference
            .collection(KEY_ROOM_MESSAGE_COLL)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // when a user sends the first message, room is displayed
                if (querySnapshot.size() == 1) {
                    val chatRoom = ChatRoom(id, name, members, timestamp, content, from, type)
                    // if room has been exist, return
                    rooms.forEach { if (it.id == id) return@addOnSuccessListener }
                    rooms.add(chatRoom)
                    rooms.sortByDescending { it.timestamp }
                    callback()
                }
                // when a user sends the next message, room is moved to top
                else if (querySnapshot.size() > 1) {
                    rooms.forEach {
                        if (it.id == id) {
                            it.timestamp = timestamp
                            it.content = content
                            it.from = from
                            it.type = type
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