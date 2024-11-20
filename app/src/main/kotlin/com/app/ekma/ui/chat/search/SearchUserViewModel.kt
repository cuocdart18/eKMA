package com.app.ekma.ui.chat.search

import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.Resource
import com.app.ekma.common.genChatRoomId
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.data.models.MiniStudent
import com.app.ekma.data.models.service.IScoreService
import com.app.ekma.firebase.KEY_MESSAGE_TIMESTAMP_DOC
import com.app.ekma.firebase.KEY_ROOMS_COLL
import com.app.ekma.firebase.KEY_ROOM_ID
import com.app.ekma.firebase.KEY_ROOM_MEMBERS
import com.app.ekma.firebase.KEY_USERS_COLL
import com.app.ekma.firebase.KEY_USER_ID
import com.app.ekma.firebase.firestore
import com.google.firebase.firestore.FieldValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchUserViewModel @Inject constructor(
    private val scoreService: IScoreService
) : BaseViewModel() {
    override val TAG = SearchUserViewModel::class.java.simpleName

    // instant EditText
    internal val queryFlow = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1
    )

    val searchResult = queryFlow
        .debounce(400L)
        .filterNot { it.isBlank() }
        .distinctUntilChanged()

    private val _showSearchIcon = MutableStateFlow(false)
    val showSearchIcon: StateFlow<Boolean>
        get() = _showSearchIcon.asStateFlow()

    fun setShowSearchIcon(hasShow: Boolean) {
        viewModelScope.launch {
            _showSearchIcon.value = hasShow
        }
    }

    private val _miniStudentsRes = MutableStateFlow<List<MiniStudent>>(listOf())
    val miniStudentsRes: StateFlow<List<MiniStudent>>
        get() = _miniStudentsRes.asStateFlow()

    fun onSearchEditTextObserved(query: String) {
        viewModelScope.launch {
            val miniStudentsRes = scoreService.getMiniStudentsByQuery(query)
            if (miniStudentsRes is Resource.Success && miniStudentsRes.data != null) {
                withContext(Dispatchers.Default) {
                    firestore.collection(KEY_USERS_COLL)
                        .whereNotEqualTo(KEY_USER_ID, ProfileSingleton().studentCode)
                        .get()
                        .addOnSuccessListener { snap ->
                            val studentIdList = snap.toList().map { it.id }
                            _miniStudentsRes.value = miniStudentsRes.data.filter {
                                studentIdList.contains(it.id)
                            }
                        }.addOnFailureListener {
                            _miniStudentsRes.value = listOf()
                        }
                }
            } else {
                _miniStudentsRes.value = listOf()
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