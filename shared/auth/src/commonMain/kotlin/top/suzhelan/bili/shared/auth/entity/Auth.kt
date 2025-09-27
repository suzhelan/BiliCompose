package top.suzhelan.bili.shared.auth.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UniversalLoginResult(
    // 合并共有字段
    @SerialName("cookie_info") val cookieInfo: CookieInfo = CookieInfo(),
    @SerialName("is_new") val isNew: Boolean = false,
    @SerialName("sso") val sso: List<String> = listOf(),
    @SerialName("token_info") val tokenInfo: TokenInfo = TokenInfo(),
    // 处理差异字段
    @SerialName("is_tourist") val isTourist: Boolean? = null,  // 仅LoginResult
    @SerialName("message") val message: String? = null,        // 仅LoginResult
    @SerialName("status") val status: Int? = null,             // 仅LoginResult
    @SerialName("url") val url: String? = null,                // 仅LoginResult
    @SerialName("hint") val hint: String? = null,              // 仅TvLoginResult
    // 处理字段层级差异
    @SerialName("access_token") val accessToken: String? = null,    // TvLoginResult根节点
    @SerialName("refresh_token") val refreshToken: String? = null,  // TvLoginResult根节点
    @SerialName("mid") val mid: Int? = null                         // TvLoginResult根节点
) {

    @Serializable
    data class CookieInfo(
        @SerialName("cookies") val cookies: List<Cooky> = emptyList(),
        @SerialName("domains") val domains: List<String> = emptyList()
    )

    @Serializable
    data class TokenInfo(
        @SerialName("access_token") val accessToken: String = "",
        @SerialName("expires_in") val expiresIn: Int = 0,
        @SerialName("mid") val mid: Int = 0,
        @SerialName("refresh_token") val refreshToken: String = "",
        @SerialName("region") val region: String = "",
        @SerialName("fast_login_token") val fastLoginToken: String = ""
    )

    @Serializable
    data class Cooky(
        @SerialName("expires") val expires: Int = 0,
        @SerialName("http_only") val httpOnly: Int = 0,
        @SerialName("name") val name: String = "",
        @SerialName("secure") val secure: Int = 0,
        @SerialName("value") val value: String = "",
        @SerialName("same_site") val sameSite: Int = 0
    )
}


@Serializable
data class LoginResult(
    @SerialName("cookie_info") val cookieInfo: CookieInfo?,
    @SerialName("is_new") val isNew: Boolean,
    @SerialName("is_tourist") val isTourist: Boolean,
    @SerialName("message") val message: String,
    @SerialName("sso") val sso: List<String>?,
    @SerialName("status") val status: Int,
    @SerialName("token_info") val tokenInfo: TokenInfo?,
    @SerialName("url") val url: String
) {
    @Serializable
    data class CookieInfo(
        @SerialName("cookies") val cookies: List<Cooky>,
        @SerialName("domains") val domains: List<String>
    )

    @Serializable
    data class TokenInfo(
        @SerialName("access_token") val accessToken: String,
        @SerialName("expires_in") val expiresIn: Int,
        @SerialName("fast_login_token") val fastLoginToken: String,
        @SerialName("mid") val mid: Int,
        @SerialName("refresh_token") val refreshToken: String,
        @SerialName("region") val region: String
    )

    @Serializable
    data class Cooky(
        @SerialName("expires") val expires: Int,
        @SerialName("http_only") val httpOnly: Int,
        @SerialName("name") val name: String,
        @SerialName("same_site") val sameSite: Int,
        @SerialName("secure") val secure: Int,
        @SerialName("value") val value: String
    )
}


@Serializable
data class TvLoginResult(
    @SerialName("access_token")
    val accessToken: String, // c2e0e18fde02883a55c0b123ab3d7982
    @SerialName("cookie_info")
    val cookieInfo: CookieInfo,
    @SerialName("expires_in")
    val expiresIn: Int, // 15552000
    @SerialName("hint")
    val hint: String,
    @SerialName("is_new")
    val isNew: Boolean, // false
    @SerialName("mid")
    val mid: Int, // 479396940
    @SerialName("refresh_token")
    val refreshToken: String, // fe3c92b83a3a5d4ab99430dd8b702282
    @SerialName("sso")
    val sso: List<String>,
    @SerialName("token_info")
    val tokenInfo: TokenInfo
) {
    @Serializable
    data class CookieInfo(
        @SerialName("cookies")
        val cookies: List<Cooky>,
        @SerialName("domains")
        val domains: List<String>
    )

    @Serializable
    data class TokenInfo(
        @SerialName("access_token")
        val accessToken: String, // c2e0e18fde02883a55c0b123ab3d7982
        @SerialName("expires_in")
        val expiresIn: Int, // 15552000
        @SerialName("mid")
        val mid: Int, // 479396940
        @SerialName("refresh_token")
        val refreshToken: String, // fe3c92b83a3a5d4ab99430dd8b702282
        @SerialName("region")
        val region: String // CN
    )

    @Serializable
    data class Cooky(
        @SerialName("expires")
        val expires: Int, // 1769581443
        @SerialName("http_only")
        val httpOnly: Int, // 1
        @SerialName("name")
        val name: String, // SESSDATA
        @SerialName("secure")
        val secure: Int, // 1
        @SerialName("value")
        val value: String // b7fd3c4e%2C1769581443%2C2a1d9882
    )
}



