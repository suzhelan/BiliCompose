package top.sacz.bili.api.proto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Fawkes协议回复消息
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FawkesReply(
    /**  配置信息字段 */
    @ProtoNumber(1)
    val config: String,

    /** 特征字段（ff） */
    @ProtoNumber(2)
    val ff: String,

    /** 设备标识字段（dd） */
    @ProtoNumber(3)
    val dd: String
)

/**
 * Fawkes协议请求消息
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FawkesReq(
    /**  appKey */
    @ProtoNumber(1)
    val appkey: String,

    /** 运行环境标识 写prod */
    @ProtoNumber(2)
    val env: String,

    /**  会话标识（proto字段名为session_id） */
    @ProtoNumber(3)
    val sessionId: String
)
