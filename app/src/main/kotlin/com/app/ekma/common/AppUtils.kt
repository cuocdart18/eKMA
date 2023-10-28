package com.app.ekma.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.app.ekma.data.models.ChatRoom
import com.app.ekma.data.models.Message
import com.app.ekma.data.models.Score
import com.app.ekma.firebase.KEY_MESSAGE_CONTENT_DOC
import com.app.ekma.firebase.KEY_MESSAGE_FROM_DOC
import com.app.ekma.firebase.KEY_MESSAGE_SEEN_DOC
import com.app.ekma.firebase.KEY_MESSAGE_TIMESTAMP_DOC
import com.app.ekma.firebase.KEY_MESSAGE_TYPE_DOC
import com.app.ekma.firebase.KEY_ROOM_MEMBERS
import com.app.ekma.firebase.KEY_USERS_COLL
import com.app.ekma.firebase.KEY_USER_NAME
import com.app.ekma.firebase.MSG_AUDIO_CALL_TYPE
import com.app.ekma.firebase.MSG_VIDEO_CALL_TYPE
import com.app.ekma.firebase.firestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import kotlin.math.round

// Global method
suspend fun parseDataToChatRoom(
    document: DocumentSnapshot,
    myStudentCode: String
): Deferred<ChatRoom> {
    return coroutineScope {
        async {
            val id = document.id
            val members = document.get(KEY_ROOM_MEMBERS) as List<String>
            val name = formatMembersToRoomName(removeMyStudentCode(members, myStudentCode))
            val serverTimestamp = document.getTimestamp(KEY_MESSAGE_TIMESTAMP_DOC)
            val timestamp = serverTimestamp?.toDate() ?: Date()
            val content = document.get(KEY_MESSAGE_CONTENT_DOC).toString()
            val from = document.get(KEY_MESSAGE_FROM_DOC).toString()
            val type = (document.getLong(KEY_MESSAGE_TYPE_DOC) ?: 1).toInt()
            val seen = document.get(KEY_MESSAGE_SEEN_DOC)?.let {
                it as MutableList<String>
            } ?: mutableListOf()
            ChatRoom(id, name, members, timestamp, content, from, type, seen)
        }
    }
}

fun parseDataToMessage(document: DocumentSnapshot): Message {
    val id = document.id
    val serverTimestamp = document.getTimestamp(KEY_MESSAGE_TIMESTAMP_DOC)
    val timestamp = serverTimestamp?.toDate() ?: Date()
    val content = document.get(KEY_MESSAGE_CONTENT_DOC).toString()
    val from = document.get(KEY_MESSAGE_FROM_DOC).toString()
    val type = document.getLong(KEY_MESSAGE_TYPE_DOC)?.toInt() ?: TEXT_MSG
    val seen = document.get(KEY_MESSAGE_SEEN_DOC) as MutableList<String>
    return Message(id, timestamp, content, from, type, seen)
}

fun checkCallPermission(context: Context, type: String): Boolean {
    return when (type) {
        MSG_AUDIO_CALL_TYPE -> {
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            )
        }

        MSG_VIDEO_CALL_TYPE -> {
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            )
        }

        else -> {
            false
        }
    }
}

fun getExtensionFile(type: String) = type.split("/").last()

inline fun sdk28AndUp(onSdk28: () -> Unit) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        onSdk28()
    }
}

inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29()
    } else null
}

fun genChatRoomId(studentId: String, myStudentId: String): String {
    val compareStatus = myStudentId.compareTo(studentId)
    return if (compareStatus == 0) {
        "$studentId$myStudentId"
    } else if (compareStatus > 0) {
        "$studentId$myStudentId"
    } else {
        "$myStudentId$studentId"
    }
}

suspend fun formatMembersToRoomName(members: List<String>): String {
    return firestore.collection(KEY_USERS_COLL)
        .document(members.first())
        .get()
        .await()
        .get(KEY_USER_NAME).toString()
}

fun removeMyStudentCode(members: List<String>, myStudentCode: String) =
    members.filter { it != myStudentCode }

fun getCachedRecordDirPath(context: Context): String {
    return "${context.cacheDir.absolutePath}/records"
}

fun getPersistentRecordDirPath(context: Context): String {
    return "${context.filesDir.absolutePath}/records"
}

fun convertPeriodToTime(data: String): String =
    when (data) {
        "1" -> "07:00"
        "3" -> "09:25"
        "4" -> "09:35"
        "6" -> "12:00"
        "7" -> "12:30"
        "9" -> "14:55"
        "10" -> "15:05"
        "12" -> "17:30"
        "13" -> "18:00"
        "16" -> "21:15"
        else -> "invalid data"
    }

/*
*   get
*   start time with key = "start"
*   end time with key = "end"
* */
fun convertPeriodsToStartEndTime(data: String): Map<String, String> {
    val periods = data
        .trim()
        .splitToSequence(',')
        .toList()
    return mapOf(
        "start" to convertPeriodToTime(periods[0]),
        "end" to convertPeriodToTime(periods[periods.size - 1])
    )
}

fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

fun jsonObjectToString(data: Any): String {
    val gson = Gson()
    return gson.toJson(data)
}

inline fun <reified T> jsonStringToObject(data: String): T {
    val gson = Gson()
    return gson.fromJson(data, T::class.java)
}

fun toDateTime(date: LocalDate, time: LocalTime): LocalDateTime =
    LocalDateTime.of(date, time)

suspend fun gpaCalculator(scores: List<Score>): Double {
    return withContext(Dispatchers.Default) {
        var avgScoreWithCreditTotal = 0.00
        var creditTotal = 0
        scores.forEach {
            if (!isExcludedSubjects(it.subject.id)) {
                avgScoreWithCreditTotal += alphabetScoreToFourCoefficientConverter(it.alphabetScore) * it.subject.numberOfCredits
                creditTotal += it.subject.numberOfCredits
            }
        }
        round((avgScoreWithCreditTotal / creditTotal) * 100) / 100
    }
}

private fun alphabetScoreToFourCoefficientConverter(alphabetScore: String) =
    when (alphabetScore) {
        "A+" -> 4.00
        "A" -> 3.80
        "B+" -> 3.50
        "B" -> 3.00
        "C+" -> 2.50
        "C" -> 2.00
        "D+" -> 1.50
        "D" -> 1.00
        "F" -> 0.00
        else -> 0.00
    }

private fun isExcludedSubjects(id: String): Boolean {
    val excludedSubject = setOf(
        "ATCBNN1",  // TA 1
        "ATCBNN2",  // TA 2
        "ATCBNN6",  // TA 3
        "ATQGTC1",  // GDTC 1
        "ATQGTC2",  // GDTC 2
        "ATQGTC3",  // GDTC 3
        "ATQGTC4",  // GDTC 4
        "ATQGTC5"   // GDTC 5
    )
    return excludedSubject.contains(id)
}

fun formatRecordingTimer(duration: Int): String {
    val millis = (duration % 1000) / 10
    val seconds = (duration / 1000) % 60
    val minutes = (duration / (1000 * 60)) % 60
    val hours = (duration / (1000 * 60 * 60))
    val formatted = if (hours > 0) {
        "%02d:%02d:%02d.%02d".format(hours, minutes, seconds, millis)
    } else {
        "%02d:%02d.%02d".format(minutes, seconds, millis)
    }
    return formatted
}

fun formatAudioDuration(duration: Int): String {
//    val durationL = duration.toLong()
    val minutes = (duration / (1000 * 60)) % 60
//    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationL)
    val seconds = (duration / 1000) % 60
//    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationL) - TimeUnit.MINUTES.toSeconds(minutes)
    val hours = (duration / (1000 * 60 * 60))
    val formatted = if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
    return formatted
}