package top.sacz.bili.api.headers


import io.ktor.util.encodeBase64
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import top.sacz.bili.api.proto.FawkesReq
import top.sacz.bili.api.proto.Locale
import top.sacz.bili.api.proto.LocaleIds
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


val commonHeaders: MutableMap<String, String>
    get() {
        val result = mutableMapOf(
            "app-key" to "android64",
            "bili-http-engine" to "ignet",
            "buvid" to BiliHeaders.buvid,
            "env" to "prod",
            "fp_local" to "c8c083bf5dc97732052c66ff0260851a2024110401353817dd863b003f0a9388",
            "fp_remote" to "c8c083bf5dc97732052c66ff0260851a2024110401353817dd863b003f0a9388",
//            "guestid" to "23797428340613",
            "session_id" to BiliHeaders.sessionId,
            "user-agent" to BiliHeaders.userAgent,
            "x-bili-aurora-eid" to BiliHeaders.auroraEid,
            "x-bili-locale-bin" to BiliHeaders.Bin.localBin,
            "x-bili-metadata-ip-region" to "CN",
            "x-bili-metadata-legal-region" to "CN",
//            "x-bili-mid" to "479396940",
//            "x-bili-ticket" to "eyJhbGciOiJIUzI1NiIsImtpZCI6InMwMyIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NDM3MjcxNzAsImlhdCI6MTc0MzY5ODA3MCwiYnV2aWQiOiJYVTVDNzg2NUUzREE2NzAwNENCMkFCNkFFNTY3OTZCRTM0RTVEIn0.ytZygV5hTYkulQ6V9wUT3BC1k-zQxqnJMgDOZCYPMOw",
            "x-bili-trace-id" to BiliHeaders.traceId
        )
        return result
    }


val grpcHeaders: MutableMap<String, String>
    get() {
        val result = mutableMapOf(
            "user-agent" to "${BiliHeaders.userAgent} grpc-java-cronet/1.36.1",
            "x-bili-gaia-vtoken" to "",
            "x-bili-aurora-eid" to BiliHeaders.auroraEid,
            "x-bili-mid" to "用户uid",
            "x-bili-aurora-zone" to "",
            "x-bili-trace-id" to BiliHeaders.traceId,
        )
        if (false) {
            result["authorization"] = "identify_v1 {access_key}"
        }
        result["buvid"] = BiliHeaders.buvid
        result["bili-http-engine"] = "cronet"
        result["te"] = "trailers"
        result["x-bili-fawkes-req-bin"] = BiliHeaders.Bin.fawkesReqBin
        return result
    }

object BiliHeaders {
    val userAgent =
        "Mozilla/5.0 BiliDroid/1.46.2 (bbcallen@gmail.com) os/android model/vivo mobi_app/android_hd build/2001100 channel/yingyongbao innerVer/2001100 osVer/14 network/2"
    val buvid: String by lazy { generateBuvid() }
    val traceId: String by lazy { genTraceId() }
    val auroraEid: String by lazy { genAuroraEid(0L) }
    val deviceId: String by lazy { genDeviceId() }
    val statistics: String by lazy {
    """
    {"appId":1,"platform":3,"version":"7.27.0","abtest":""}
    """ }
    val sessionId: String by lazy { genSessionId() }
    private val random = Random.Default

    object Bin {
        val localBin: String by lazy { genLocalBin() }
        val fawkesReqBin: String by lazy { genFawkesReqBin() }


        @OptIn(ExperimentalSerializationApi::class)
        private fun genFawkesReqBin(): String {
            val fawkesReq = FawkesReq(
                appkey = "1d8b6e7d45233436", env = "prod", sessionId = "随机八位字符串"
            )
            val byteArray = ProtoBuf.encodeToByteArray(FawkesReq.serializer(), fawkesReq)
            val base64 = byteArray.encodeBase64()
            return base64.replace("=", "")
        }

        @OptIn(ExperimentalSerializationApi::class)
        private fun genLocalBin(): String {
            val localProto = Locale(
                cLocale = LocaleIds(language = "zh", script = "", region = "CN"),
                sLocale = LocaleIds(language = "zh", script = "", region = "CN"),
                simCode = "",
                timezone = "Asia/Shanghai"
            )
            val byteArray = ProtoBuf.encodeToByteArray(Locale.serializer(), localProto)
            val base64 = byteArray.encodeBase64()
            return base64.replace("=", "")
        }
    }

    //生成随机八位hex
    private fun genSessionId(): String {
        return genRandomString(8)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun genAuroraEid(uid: Long): String {
        if (uid == 0L) return ""
        // 1. 将 UID 转为字节数组
        val uidStr = uid.toString()
        val midBytes = uidStr.toByteArray()
        // 2. 异或操作
        val key = "ad1va46a7lza".toByteArray()
        val result = ByteArray(midBytes.size)
        for (i in midBytes.indices) {
            val keyIndex = i % key.size
            val keyByte = key[keyIndex]
            // 需要处理无符号异或操作
            result[i] = (midBytes[i].toInt() xor keyByte.toInt()).toByte()
        }
        // 3. Base64编码（不带padding）
        return Base64.encode(result).replace("=", "")
    }

    @OptIn(ExperimentalTime::class)
    private fun genTraceId(): String {
        // 1. 生成 32 位随机字符串 random_id, Charset 为 0~9, a~z.
        val randomId = genRandomString(32)
        val randomTraceId = StringBuilder(40)
        // 2. 取 random_id 前 24 位, 作为 random_trace_id.
        randomTraceId.append(randomId.substring(0, 24))
        // 3. 初始化一个长度为 3 的数组 bArr, 初始值都为 0.
        val bArr = IntArray(3) { 0 }
        var ts = Clock.System.now().epochSeconds
        // 使用循环从高位到低位遍历 bArr 数组, 循环体内执行以下逻辑:
        //  - 首先将 ts 右移 8 位
        //  - 然后根据条件向 bArr 的第 i 位赋值:
        //    - 如果 (ts / 128) % 2 的结果为 0, 则 bArr[i] = ts % 256
        //    - 否则 bArr[i] = ts % 256 - 256
        for (i in 2 downTo 0) {
            ts = ts shr 8
            bArr[i] = if ((ts / 128) % 2 == 0.toLong()) {
                (ts % 256).toInt()
            } else {
                (ts % 256 - 256).toInt()
            }
        }
        // 4. 将数组 bArr 中的每个元素逐个转换为两位的十六进制字符串并追加到 random_trace_id 中.
        for (i in 0 until 3) {
            val value = bArr[i] and 0xFF // 确保值在 0-255 范围（处理负数）
            val hex = value.toString(16).padStart(2, '0') // 转换为两位十六进制
            randomTraceId.append(hex)
        }
        // 5. 将 random_id 的第 31, 32 个字符追加到 random_trace_id 中, 此时 random_trace_id 生成完毕, 应当为 32 位长度.
        randomTraceId.append(randomId.substring(30, 32))
        // 6. 最后, 按 `{random_trace_id}:{random_trace_id[16..32]}:0:0` 的顺序拼接起来, 即为 x-bili-trace-id
        val randomTraceIdFinal = StringBuilder(64)
        randomTraceIdFinal.append(randomTraceId.toString())
        randomTraceIdFinal.append(":")
        randomTraceIdFinal.append(randomTraceId.substring(16, 32))
        randomTraceIdFinal.append(":0:0")
        return randomTraceIdFinal.toString()
    }

    private fun genRandomString(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('0'..'9')
        return (1..length).map {
            Random.nextInt(0, charPool.size)
        }.map(charPool::get).joinToString("")
    }

    private fun generateBuvid(): String {
        val md5Str = List(32) { random.nextInt(16).toString(16) }.joinToString("").uppercase()
        return "XY${md5Str[2]}${md5Str[12]}${md5Str[22]}$md5Str"
    }

    @OptIn(ExperimentalTime::class)
    private fun genDeviceId(): String {
        val yyyyMMddHHmmss =
            Clock.System.now().toString().replace(Regex("[-:TZ]"), "").substring(0, 14)
        val randomHex32 = List(32) { random.nextInt(16).toString(16) }.joinToString("")
        val randomHex16 = List(16) { random.nextInt(16).toString(16) }.joinToString("")
        val deviceID = randomHex32 + yyyyMMddHHmmss + randomHex16
        val bytes = deviceID.chunked(2).map { it.toInt(16) }
        val checksumValue = bytes.sum()
        val check = checksumValue.toString(16).takeLast(2)
        return deviceID + check
    }
}