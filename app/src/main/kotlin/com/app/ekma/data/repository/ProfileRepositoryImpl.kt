package com.app.ekma.data.repository

import com.app.ekma.base.repositories.BaseRepositories
import com.app.ekma.firebase.KEY_USER_DOB
import com.app.ekma.firebase.KEY_USER_GENDER
import com.app.ekma.firebase.KEY_USER_ID
import com.app.ekma.firebase.KEY_USER_NAME
import com.app.ekma.common.Resource
import com.app.ekma.common.jsonStringToObject
import com.app.ekma.data.data_source.apis.EKmaAPI
import com.app.ekma.data.data_source.app_data.IDataLocalManager
import com.app.ekma.data.models.Profile
import com.app.ekma.data.models.repository.IProfileRepository
import com.app.ekma.firebase.KEY_USERS_COLL
import com.app.ekma.firebase.KEY_USER_STATUS
import com.app.ekma.firebase.KEY_USER_TOKEN
import com.app.ekma.firebase.firestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val eKmaApi: EKmaAPI,
    private val dataLocalManager: IDataLocalManager
) : BaseRepositories(), IProfileRepository {

    override suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<Profile> {
        return safeApiCall {
            eKmaApi.getProfile(username, password, hashed).toProfile()
        }
    }

    override suspend fun saveProfile(profile: Profile) = withContext(Dispatchers.Default) {
        dataLocalManager.saveProfile(profile.toProfileShPref())
    }

    override suspend fun saveProfileToFirestore(profile: Profile) {
        withContext(Dispatchers.IO) {
            val token = FirebaseMessaging.getInstance().token.await()
            val profileMap = mapOf(
                KEY_USER_ID to profile.studentCode,
                KEY_USER_NAME to profile.displayName,
                KEY_USER_DOB to profile.birthday,
                KEY_USER_GENDER to profile.gender,
                KEY_USER_TOKEN to token
            )
            firestore.collection(KEY_USERS_COLL)
                .document(profile.studentCode)
                .set(profileMap)
                .await()
        }
    }

    override suspend fun clearProfile() {
        dataLocalManager.saveProfile("")
    }

    override suspend fun clearFcmToken(myStudentCode: String) {
        withContext(Dispatchers.IO) {
            val deleteUserToken = mapOf(
                KEY_USER_TOKEN to FieldValue.delete()
            )
            firestore.collection(KEY_USERS_COLL)
                .document(myStudentCode)
                .update(deleteUserToken)
        }
    }

    override suspend fun getFcmToken(code: String): String {
        return withContext(Dispatchers.IO) {
            firestore.collection(KEY_USERS_COLL)
                .document(code)
                .get()
                .await()
                .get(KEY_USER_TOKEN).toString()
        }
    }

    override suspend fun updateFcmTokenToFirestore(myStudentCode: String) {
        withContext(Dispatchers.IO) {
            val token = FirebaseMessaging.getInstance().token.await()
            val tokenMap = mapOf(
                KEY_USER_TOKEN to token
            )
            firestore.collection(KEY_USERS_COLL)
                .document(myStudentCode)
                .update(tokenMap)
                .await()
        }
    }

    override suspend fun getProfile(): Profile {
        return withContext(Dispatchers.Default) {
            jsonStringToObject(dataLocalManager.getProfile())
        }
    }
}