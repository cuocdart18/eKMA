package com.app.ekma.data.repository

import android.content.Context
import androidx.work.WorkManager
import com.app.ekma.base.repositories.BaseRepositories
import com.app.ekma.common.APP_EXTERNAL_MEDIA_FOLDER
import com.app.ekma.common.EXTERNAL_AUDIO_FOLDER
import com.app.ekma.common.Resource
import com.app.ekma.data.data_source.database.daos.NoteDao
import com.app.ekma.data.models.Note
import com.app.ekma.data.models.repository.INoteRepository
import com.app.ekma.firebase.KEY_NOTES_COLL
import com.app.ekma.firebase.KEY_NOTE_AUDIO_NAME
import com.app.ekma.firebase.KEY_NOTE_CONTENT
import com.app.ekma.firebase.KEY_NOTE_DATE
import com.app.ekma.firebase.KEY_NOTE_ID
import com.app.ekma.firebase.KEY_NOTE_IS_DONE
import com.app.ekma.firebase.KEY_NOTE_TIME
import com.app.ekma.firebase.KEY_NOTE_TITLE
import com.app.ekma.firebase.KEY_NOTE_TYPE
import com.app.ekma.firebase.KEY_USERS_COLL
import com.app.ekma.firebase.firestore
import com.app.ekma.work.WorkRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : BaseRepositories(), INoteRepository {

    override suspend fun insertNote(note: Note, myStudentCode: String) {
        safeApiCall {
            val noteMap = mapOf(
                KEY_NOTE_ID to note.id,
                KEY_NOTE_TITLE to note.title,
                KEY_NOTE_CONTENT to note.content,
                KEY_NOTE_AUDIO_NAME to note.audioName,
                KEY_NOTE_DATE to note.date,
                KEY_NOTE_TIME to note.time,
                KEY_NOTE_IS_DONE to note.isDone,
                KEY_NOTE_TYPE to note.type
            )
            firestore.collection(KEY_USERS_COLL)
                .document(myStudentCode)
                .collection(KEY_NOTES_COLL)
                .document(note.id.toString())
                .set(noteMap)
        }
    }

    override suspend fun getNotes(myStudentCode: String): Resource<List<Note>> {
        return safeApiCall {
            val notes = mutableListOf<Note>()
            firestore.collection(KEY_USERS_COLL)
                .document(myStudentCode)
                .collection(KEY_NOTES_COLL)
                .get()
                .await()
                .documents
                .onEach {
                    val id = it.getLong(KEY_NOTE_ID)?.toInt() ?: 0
                    val title = it.getString(KEY_NOTE_TITLE).toString()
                    val content = it.getString(KEY_NOTE_CONTENT).toString()
                    val audioPath = it.getString(KEY_NOTE_AUDIO_NAME).toString()
                    val date = it.getString(KEY_NOTE_DATE).toString()
                    val time = it.getString(KEY_NOTE_TIME).toString()
                    val isDone = it.getBoolean(KEY_NOTE_IS_DONE) ?: false
                    val type = it.getLong(KEY_NOTE_TYPE)?.toInt() ?: 1
                    val note = Note(title, content, audioPath, date, time)
                    note.id = id
                    note.isDone = isDone
                    notes.add(note)
                }
            notes
        }
    }

    override suspend fun deleteNote(note: Note, myStudentCode: String) {
        safeApiCall {
            firestore.collection(KEY_USERS_COLL)
                .document(myStudentCode)
                .collection(KEY_NOTES_COLL)
                .document(note.id.toString())
                .delete()
        }
    }

    override suspend fun deleteAudioNote(context: Context, audioName: String, studentCode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (audioName.isNotEmpty()) {
                // delete local audio file
                File(
                    context.getExternalFilesDir("$APP_EXTERNAL_MEDIA_FOLDER/$studentCode/$EXTERNAL_AUDIO_FOLDER"),
                    audioName
                ).delete()
                // delete remote audio file
                val workManager = WorkManager.getInstance(context)
                WorkRunner.runDeleteAudioNoteWorker(workManager, studentCode, audioName)
            }
        }
    }

    override suspend fun updateNote(note: Note, myStudentCode: String) {
        safeApiCall {
            val noteMap = mapOf(
                KEY_NOTE_ID to note.id,
                KEY_NOTE_TITLE to note.title,
                KEY_NOTE_CONTENT to note.content,
                KEY_NOTE_AUDIO_NAME to note.audioName,
                KEY_NOTE_DATE to note.date,
                KEY_NOTE_TIME to note.time,
                KEY_NOTE_IS_DONE to note.isDone,
                KEY_NOTE_TYPE to note.type
            )
            firestore.collection(KEY_USERS_COLL)
                .document(myStudentCode)
                .collection(KEY_NOTES_COLL)
                .document(note.id.toString())
                .update(noteMap)
        }
    }

    override suspend fun deleteNotes(myStudentCode: String) {
    }
}