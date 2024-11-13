package com.app.ekma.ui.main

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.pattern.singleton.ConnReferenceKey
import com.app.ekma.common.Data
import com.app.ekma.common.GET_SCHEDULE_WORKER_TAG
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.common.UNIQUE_GET_SCHEDULE_WORK_NAME
import com.app.ekma.common.pattern.singleton.CurrentEventsRefresher
import com.app.ekma.common.pattern.singleton.GetScheduleNoteSuccess
import com.app.ekma.data.models.service.ILoginService
import com.app.ekma.data.models.service.INoteService
import com.app.ekma.data.models.service.IProfileService
import com.app.ekma.data.models.service.IScheduleService
import com.app.ekma.firebase.ACTIVE_STATUS
import com.app.ekma.firebase.CONNECTED_REFERENCE
import com.app.ekma.firebase.CONNECTIONS
import com.app.ekma.firebase.LAST_ONLINE
import com.app.ekma.firebase.connectedRefListener
import com.app.ekma.firebase.database
import com.app.ekma.work.WorkRunner
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginService: ILoginService,
    private val noteService: INoteService,
    private val scheduleService: IScheduleService,
    private val profileService: IProfileService
) : BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName

    private lateinit var connectedRef: DatabaseReference

    fun authForUserEntryApp(
        callback: (state: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val state = loginService.getLoginState()
            // update device's token to firestore
            if (state) {
                launch { profileService.updateFcmToken() }
            }
            callback(state)
        }
    }

    fun firstInitialize(context: Context) {
        runWorkerIfFailure(context)
        getLocalData()
        regisActiveValueEventListener()
    }

    private fun runWorkerIfFailure(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val workManager = WorkManager.getInstance(context)
            val workQuery = WorkQuery.Builder
                .fromTags(listOf(GET_SCHEDULE_WORKER_TAG))
                .addStates(listOf(WorkInfo.State.FAILED))
                .addUniqueWorkNames(
                    listOf(UNIQUE_GET_SCHEDULE_WORK_NAME)
                )
                .build()
            workManager.getWorkInfos(workQuery)
                .get()
                .forEach { workInfo ->
                    logError("tag=${workInfo.tags} and id=${workInfo.id} with state=${workInfo.state.name}")
                    if (workInfo.tags.contains(GET_SCHEDULE_WORKER_TAG)) {
                        WorkRunner.runGetScheduleWorker(workManager)
                    }
                }
        }
    }

    private fun getLocalData() {
        viewModelScope.launch(Dispatchers.IO) {
            ProfileSingleton.setData(profileService.getProfile())
            Data.getLocalData(noteService, scheduleService) {
                GetScheduleNoteSuccess.setData(true)
                CurrentEventsRefresher.setData(true)
            }
        }
    }

    private fun regisActiveValueEventListener() {
        viewModelScope.launch {
            val myStudentCode = profileService.getProfile().studentCode
            val myConnectionsRef =
                database.child(ACTIVE_STATUS).child(myStudentCode).child(CONNECTIONS)
            val lastOnlineRef =
                database.child(ACTIVE_STATUS).child(myStudentCode).child(LAST_ONLINE)
            connectedRef = database.database.getReference(CONNECTED_REFERENCE)
            connectedRefListener = connectedRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.value as Boolean
                    if (connected) {
                        val conn = myConnectionsRef.push()
                        ConnReferenceKey.setData(conn.key ?: "")
                        // When this device disconnects, remove it
                        conn.onDisconnect().removeValue()
                        // When I disconnect, update the last time I was seen online
                        lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP)
                        // Add this device to my connections list
                        // this value could contain info about the device or a timestamp too
                        conn.setValue(true)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    logError("Listener was cancelled at $CONNECTED_REFERENCE")
                }
            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this::connectedRef.isInitialized) {
            connectedRef.removeEventListener(connectedRefListener)
        }
    }
}