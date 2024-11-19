package com.app.ekma.data

// A generic class that contains data and status about loading this data.
sealed class Resource<T>(
    val data: T? = null,
    val errorCode: Int? = null,
    val errorMsg: String? = null,
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class DataError<T>(errorCode: Int) : Resource<T>(null, errorCode)

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data], [message=$errorMsg]"
            is DataError -> "Error[exception=$errorCode], [message=$errorMsg]"
            is Loading<T> -> "Loading"
        }
    }
}
