package com.app.ekma.base.repositories

import android.database.sqlite.SQLiteException
import android.util.Log
import com.app.ekma.common.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

open class BaseRepositories {
    protected open val TAG = ""

    suspend fun <T> safeApiCall(call: suspend () -> T): Resource<T> {

        return withContext(Dispatchers.IO) {
            try {
                val response = call()
                Resource.Success(response)
            } catch (e: HttpException) {
                Resource.Error(errorMessage = e.message ?: "Something went wrong")
            } catch (e: IOException) {
                Resource.Error("Please check your network connection")
            } catch (e: Exception) {
                Resource.Error(errorMessage = e.message ?: "Something went wrong")
            } catch (e: SocketTimeoutException) {
                Resource.Error(errorMessage = e.message ?: "Something went wrong")
            }
        }
    }

    suspend fun <T> safeDaoCall(call: suspend () -> T): Resource<T> {

        return withContext(Dispatchers.IO) {
            try {
                val response = call()
                Resource.Success(response)
            } catch (e: SQLiteException) {
                Resource.Error(errorMessage = e.message ?: "Something went wrong")
            }
        }
    }

    // LOG
    open fun logLifecycle(msg: String) {
        Log.d(TAG, "$msg $TAG")
    }

    open fun logError(msg: String) {
        Log.e(TAG, msg)
    }

    open fun logDebug(msg: String) {
        Log.d(TAG, msg)
    }

    open fun logInfo(msg: String) {
        Log.i(TAG, msg)
    }

    open fun logWarning(msg: String) {
        Log.w(TAG, msg)
    }
}