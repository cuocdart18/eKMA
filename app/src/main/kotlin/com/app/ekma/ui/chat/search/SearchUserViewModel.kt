package com.app.ekma.ui.chat.search

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.firebase.KEY_MESSAGE_TIMESTAMP_DOC
import com.app.ekma.firebase.KEY_ROOMS_COLL
import com.app.ekma.firebase.KEY_ROOM_ID
import com.app.ekma.firebase.KEY_ROOM_MEMBERS
import com.app.ekma.firebase.KEY_USER_ID
import com.app.ekma.common.Resource
import com.app.ekma.common.genChatRoomId
import com.app.ekma.data.models.MiniStudent
import com.app.ekma.data.models.service.IScoreService
import com.app.ekma.firebase.KEY_USERS_COLL
import com.app.ekma.firebase.firestore
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchUserViewModel @Inject constructor(
    private val scoreService: IScoreService
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

    private suspend fun filterAvailableUsers(
        miniStudents: List<MiniStudent>,
        callback: (miniStudents: List<MiniStudent>) -> Unit
    ) {
        withContext(Dispatchers.Default) {
            val studentIdList = firestore.collection(KEY_USERS_COLL)
                .whereNotEqualTo(KEY_USER_ID, ProfileSingleton().studentCode)
                .get()
                .await()
                .map { it.id }
            val res = miniStudents.filter {
                studentIdList.contains(it.id)
            }
            withContext(Dispatchers.Main) {
                callback(res)
            }
        }
    }

    fun referenceToChatRoom(
        studentCode: String,
        callback: (roomId: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val myStudentCode = ProfileSingleton().studentCode
            val roomId = genChatRoomId(studentCode, myStudentCode)
            val chatRoomDocRef = firestore.collection(KEY_ROOMS_COLL).document(roomId)
            chatRoomDocRef.get().addOnSuccessListener {
                if (it.exists()) {
                    callback(roomId)
                } else {
                    val room = mapOf(
                        KEY_ROOM_ID to roomId,
                        KEY_ROOM_MEMBERS to listOf(studentCode, myStudentCode),
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