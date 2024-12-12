package com.app.ekma.data.repository

import com.app.ekma.common.convertPeriodsToStartEndTime
import com.app.ekma.common.jsonObjectToString
import com.app.ekma.data.data_source.apis.dto.AgoraTokenRequestDto
import com.app.ekma.data.data_source.apis.dto.AgoraTokenResponseDto
import com.app.ekma.data.data_source.apis.dto.FcmDataMessageDto
import com.app.ekma.data.data_source.apis.dto.FcmDataNewMessageDto
import com.app.ekma.data.data_source.apis.dto.MessageResult
import com.app.ekma.data.data_source.apis.dto.MiniStudentDto
import com.app.ekma.data.data_source.apis.dto.PeriodDto
import com.app.ekma.data.data_source.apis.dto.ProfileDetailDto
import com.app.ekma.data.data_source.apis.dto.ProfileDto
import com.app.ekma.data.data_source.apis.dto.ScoreDto
import com.app.ekma.data.data_source.apis.dto.StudentDto
import com.app.ekma.data.data_source.apis.dto.SubjectDto
import com.app.ekma.data.data_source.database.entities.MiniStudentEntity
import com.app.ekma.data.data_source.database.entities.NoteEntity
import com.app.ekma.data.data_source.database.entities.PeriodEntity
import com.app.ekma.data.models.AgoraTokenRequest
import com.app.ekma.data.models.AgoraTokenResponse
import com.app.ekma.data.models.FcmDataMessage
import com.app.ekma.data.models.FcmDataNewMessage
import com.app.ekma.data.models.MiniStudent
import com.app.ekma.data.models.Note
import com.app.ekma.data.models.Period
import com.app.ekma.data.models.Profile
import com.app.ekma.data.models.ProfileDetail
import com.app.ekma.data.models.Score
import com.app.ekma.data.models.Student
import com.app.ekma.data.models.Subject
import com.app.ekma.data.models.User
import java.util.Date

fun MessageResult.toMessage(): String {
    return this.message
}

fun MiniStudentEntity.toMiniStudent() = MiniStudent(
    id = id,
    name = name,
    classInSchool = classInSchool,
    dateModified = dateModified
)

fun MiniStudent.toMiniStudentEntity() = MiniStudentEntity(
    id = id,
    name = name,
    classInSchool = classInSchool,
    dateModified = dateModified
)

fun MiniStudentDto.toMiniStudent() = MiniStudent(
    id = id,
    name = name,
    classInSchool = classInSchool,
    dateModified = Date()
)

fun Note.toNoteEntity(): NoteEntity {
    val noteEntity = NoteEntity(
        title = title,
        content = content,
        audioName = audioName,
        date = date,
        time = time
    )
    noteEntity.isDone = isDone
    noteEntity.id = id
    return noteEntity
}

fun NoteEntity.toNote(): Note {
    val note = Note(
        title = title,
        content = content.toString(),
        audioName = audioName.toString(),
        date = date,
        time = time
    )
    note.isDone = isDone
    note.id = id
    return note
}

fun PeriodDto.toPeriod(): Period {
    val period = Period(
        className = className,
        day = day,
        id = id,
        lesson = lesson,
        room = room,
        subjectCode = subjectCode,
        subjectName = subjectName,
        teacher = teacher
    )
    val timeMap = convertPeriodsToStartEndTime(lesson)
    period.startTime = timeMap["start"]!!
    period.endTime = timeMap["end"]!!
    return period
}

fun Period.toPeriodEntity() = PeriodEntity(
    className = className,
    day = day,
    id = id,
    lesson = lesson,
    room = room,
    subjectCode = subjectCode,
    subjectName = subjectName,
    teacher = teacher
)

fun PeriodEntity.toPeriod(): Period {
    val period = Period(
        className = className,
        day = day,
        id = id,
        lesson = lesson,
        room = room,
        subjectCode = subjectCode,
        subjectName = subjectName,
        teacher = teacher
    )
    val timeMap = convertPeriodsToStartEndTime(lesson)
    period.startTime = timeMap["start"]!!
    period.endTime = timeMap["end"]!!
    return period
}

fun ProfileDto.toProfile() = Profile(
    displayName = displayName,
    studentCode = studentCode,
    gender = gender,
    birthday = birthday
)

fun Profile.toProfileShPref(): String {
    return jsonObjectToString(this)
}

fun ProfileDetailDto.toProfileDetail() = ProfileDetail(
    displayName = displayName,
    studentCode = studentCode,
    gender = gender,
    birthday = birthday,
    hometown = hometown,
    phoneNumber = phoneNumber,
    email = email
)

fun ProfileDetail.toProfileDetailShPref(): String {
    return jsonObjectToString(this)
}

fun User.toUserShPref(): String {
    return jsonObjectToString(this)
}

fun SubjectDto.toSubject() = Subject(
    id = id,
    name = name,
    numberOfCredits = numberOfCredits
)

fun ScoreDto.toScore() = Score(
    subject = subject.toSubject(),
    firstComponentScore = firstComponentScore,
    secondComponentScore = secondComponentScore,
    examScore = examScore,
    avgScore = avgScore,
    alphabetScore = alphabetScore,
    index = index
)

fun StudentDto.toStudent() = Student(
    id = id,
    name = name,
    classInSchool = classInSchool,
    avgScore = avgScore,
    passedSubjects = passedSubjects,
    failedSubjects = failedSubjects,
    scores = scores.map { it.toScore() }
)

fun FcmDataMessage.toFcmDataMessageDto() = FcmDataMessageDto(
    token = token,
    data = data
)

fun FcmDataNewMessage.toFcmDataNewMessageDto() = FcmDataNewMessageDto(
    token = token,
    data = data
)

fun AgoraTokenRequest.toAgoraTokenRequestDto() = AgoraTokenRequestDto(
    tokenType = tokenType,
    channel = channel,
    role = role,
    uid = uid,
    expired = expired
)

fun AgoraTokenResponseDto.toAgoraTokenResponse() = AgoraTokenResponse(
    token = token
)