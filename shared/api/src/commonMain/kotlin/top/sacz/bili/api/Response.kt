package top.sacz.bili.api

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: T
)
