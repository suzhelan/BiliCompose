package top.suzhelan.bili.api.proto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

//https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/grpc_api/bilibili/metadata/locale/locale.proto

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Locale(
    @ProtoNumber(1)
    val cLocale: LocaleIds,

    @ProtoNumber(2)
    val sLocale: LocaleIds,

    @ProtoNumber(3)
    val simCode: String,

    @ProtoNumber(4)
    val timezone: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class LocaleIds(
    @ProtoNumber(1)
    val language: String,

    @ProtoNumber(2)
    val script: String,

    @ProtoNumber(3)
    val region: String
)