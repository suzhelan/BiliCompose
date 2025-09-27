package top.suzhelan.bili.biz.login.model

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
