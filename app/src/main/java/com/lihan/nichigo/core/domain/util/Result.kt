package com.lihan.nichigo.core.domain.util

sealed interface Result<out D, out E> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E>(val error: com.lihan.nichigo.core.domain.util.Error): Result<Nothing, E>
}

inline fun <T, E: Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

inline fun <T, E> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T, E: Error> Result<T, E>.onError(action: (e: Error) -> Unit): Result<T, E> {
    if (this is Result.Error) action(error)
    return this
}

typealias EmptyResult<E> = Result<Unit, E>

fun <T, E: Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map { }
}
