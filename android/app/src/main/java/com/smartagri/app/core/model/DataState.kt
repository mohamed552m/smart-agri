package com.smartagri.app.core.model

sealed interface DataState<out T> {
    data object Loading : DataState<Nothing>
    data object Empty : DataState<Nothing>
    data class Success<T>(val value: T) : DataState<T>
    data class Error(val type: DataError, val message: String? = null) : DataState<Nothing>
}

enum class DataError {
    NETWORK,
    AUTHENTICATION,
    PERMISSION_DENIED,
    INVALID_DATA,
    STALE_DATA,
    UNKNOWN,
}
