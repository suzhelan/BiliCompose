package top.sacz.bili.biz.login.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryList(
    @SerialName("common") val common: List<Country>,
    @SerialName("others") val others: List<Country>
)

@Serializable
data class Country(
    @SerialName("cname") val cname: String,
    @SerialName("country_id") val countryId: String,
    @SerialName("id") val id: Int
)

@Serializable
data class SendSmsLoginCodeResult(
    @SerialName("captcha_key")
    val captchaKey: String,
    @SerialName("recaptcha_url")
    val recaptchaUrl: String
)

@Serializable
data class SmsLoginResult(
    @SerialName("cookie_info") val cookieInfo: CookieInfo?,  // 改为可空类型
    @SerialName("is_new") val isNew: Boolean,
    @SerialName("is_tourist") val isTourist: Boolean,
    @SerialName("message") val message: String,
    @SerialName("sso") val sso: List<String>?,  // 改为可空类型
    @SerialName("status") val status: Int,
    @SerialName("token_info") val tokenInfo: TokenInfo?,  // 改为可空类型
    @SerialName("url") val url: String
)
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