package top.suzhelan.bili.biz.recvids.entity

sealed class BaseCoverItem {
    abstract val cardType: String
    abstract val cardGoto: String
    abstract val idx: Int
}

/**
 * 横幅
 */
fun BaseCoverItem.isBanner(): Boolean {
    return cardGoto == "banner" || cardType == "banner_v8"
}

/**
 * 大封面内嵌视频 类似横幅布局但不能左右滑
 */
fun BaseCoverItem.isLargeCoverOrInline(): Boolean {
    return cardType == "large_cover_v9" || cardType == "inline_av_v2"
}

/**
 * 广告
 */
fun BaseCoverItem.isAd(): Boolean {
    return cardType == "cm_v2" || cardType == "ad_av"
}

/**
 * 普通小封面视频
 */
fun BaseCoverItem.isSmallV2(): Boolean {
    return cardType == "small_cover_v2" || cardGoto == "av"
}

/**
 * 直播
 */
fun BaseCoverItem.isLive(): Boolean {
    return cardType == "small_cover_v9" || cardGoto == "live"
}