package com.example.kmatool.ui.chat.search

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.KEY_MESSAGE_TIMESTAMP_DOC
import com.example.kmatool.common.KEY_ROOMS_COLL
import com.example.kmatool.common.KEY_ROOM_ID
import com.example.kmatool.common.KEY_ROOM_MEMBERS
import com.example.kmatool.common.KEY_USERS_COLL
import com.example.kmatool.common.KEY_USER_ID
import com.example.kmatool.common.Resource
import com.example.kmatool.common.genChatRoomId
import com.example.kmatool.data.models.MiniStudent
import com.example.kmatool.data.models.service.IProfileService
import com.example.kmatool.data.models.service.IScoreService
import com.example.kmatool.firebase.firestore
import com.google.firebase.firestore.FieldValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchUserViewModel @Inject constructor(
    private val scoreService: IScoreService,
    private val profileService: IProfileService
) : BaseViewModel() {
    override val TAG = SearchUserViewModel::class.java.simpleName

    // instant EditText
    @OptIn(ObsoleteCoroutinesApi::class)
    internal val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    @OptIn(ObsoleteCoroutinesApi::class, FlowPreview::class)
    val searchResult = queryChannel
        .asFlow()
        .debounce(400L)
        .filterNot { it.isBlank() }
        .distinctUntilChanged()
        .asLiveData()

    fun onSearchEditTextObserved(
        text: String,
        callback: (miniStudents: List<MiniStudent>?) -> Unit
    ) {
        viewModelScope.launch {
            val miniStudentsRes = scoreService.getMiniStudentsByQuery(text)
            if (miniStudentsRes is Resource.Success && miniStudentsRes.data != null) {
                filterAvailableUsers(miniStudentsRes.data, callback)
            } else {
                callback(null)
            }
        }
    }

    private fun filterAvailableUsers(
        miniStudents: List<MiniStudent>,
        callback: (miniStudents: List<MiniStudent>) -> Unit
    ) {
        firestore.collection(KEY_USERS_COLL)
            .get()
            .addOnSuccessListener { documents ->
                viewModelScope.launch(Dispatchers.IO) {
                    val availableUsersId = mutableListOf<String>()
                    for (document in documents) {
                        availableUsersId.add(document.get(KEY_USER_ID).toString())
                    }
                    val filterUsers = miniStudents.filter { availableUsersId.contains(it.id) }
                    withContext(Dispatchers.Main) {
                        callback(filterUsers)
                    }
                }
            }
    }

    fun referenceToChatRoom(
        studentId: String,
        callback: (roomId: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val myStudentId = profileService.getProfile().studentCode
            val roomId = genChatRoomId(studentId, myStudentId)
            val chatRoomDocRef = firestore.collection(KEY_ROOMS_COLL).document(roomId)
            chatRoomDocRef.get().addOnSuccessListener {
                if (it.exists()) {
                    callback(roomId)
                } else {
                    val room = mapOf(
                        KEY_ROOM_ID to roomId,
                        KEY_ROOM_MEMBERS to listOf(studentId, myStudentId),
                        KEY_MESSAGE_TIMESTAMP_DOC to FieldValue.serverTimestamp()
                    )
                    chatRoomDocRef.set(room).addOnSuccessListener {
                        callback(roomId)
                    }
                }
            }
        }
    }
}