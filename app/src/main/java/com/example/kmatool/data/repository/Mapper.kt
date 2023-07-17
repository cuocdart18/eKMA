package com.example.kmatool.data.repository

import com.example.kmatool.common.convertPeriodsToStartEndTime
import com.example.kmatool.common.jsonObjectToString
import com.example.kmatool.data.data_source.apis.dto.MessageResult
import com.example.kmatool.data.data_source.apis.dto.MiniStudentDto
import com.example.kmatool.data.data_source.apis.dto.PeriodDto
import com.example.kmatool.data.data_source.apis.dto.ProfileDto
import com.example.kmatool.data.data_source.apis.dto.ScoreDto
import com.example.kmatool.data.data_source.apis.dto.StudentDto
import com.example.kmatool.data.data_source.apis.dto.SubjectDto
import com.example.kmatool.data.data_source.database.entities.MiniStudentEntity
import com.example.kmatool.data.data_source.database.entities.NoteEntity
import com.example.kmatool.data.data_source.database.entities.PeriodEntity
import com.example.kmatool.data.models.MiniStudent
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.models.Score
import com.example.kmatool.data.models.Student
import com.example.kmatool.data.models.Subject
import com.example.kmatool.data.models.User
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
        content = content,
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
