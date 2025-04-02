package top.sacz.bili.api

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.TreeMap


actual object AppSigner {

    private fun appSign(
        appKey: String,
        appSec: String,
        params: MutableMap<String, Any?>
    ): String {
        // 为请求参数进行 APP 签名
        params["appkey"] = appKey
        // 按照 key 重排参数
        val sortedParams = TreeMap<String, Any?>(params)
        // 序列化参数
        val queryBuilder = StringBuilder()
        for ((key, value) in sortedParams) {
            if (queryBuilder.isNotEmpty()) {
                queryBuilder.append('&')
            }
            queryBuilder.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                .append('=')
                .append(URLEncoder.encode(value?.toString(), StandardCharsets.UTF_8))
        }
        return generateMD5(queryBuilder.append(appSec).toString())
    }

    private fun generateMD5(
        input: String
    ): String {
        try {
            val md = MessageDigest.getInstance(
                "MD5"
            )
            val digest = md.digest(input.toByteArray())
            val sb = StringBuilder()
            for (b in digest
            ) {
                sb.append(
                    String.format(
                        "%02x", b
                    )
                )
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    actual fun sign(
        appKey: String,
        appSec: String,
        params: MutableMap<String, Any?>
    ): String {
        return appSign(appKey, appSec, params)
    }

}