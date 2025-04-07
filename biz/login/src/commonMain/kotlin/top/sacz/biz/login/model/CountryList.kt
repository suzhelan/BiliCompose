package top.sacz.biz.login.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryList(
    val common: List<Country>,
    val others: List<Country>
)

@Serializable
data class Country(
    val cname: String,
    @SerialName("country_id")
    val countryId: String,
    val id: Int
)

