package top.suzhelan.bili.biz.biliplayer.config

val codecid = mapOf(
    7 to "avc",//8K不支持
    12 to "hev",
    13 to "av1",
)

val qualityMap = mapOf(
    6 to "240P 极速",
    16 to "360P 流畅",
    32 to "480P 清晰",
    64 to "720P 高清",
    74 to "720P60 高帧率",
    80 to "1080P 高清",
    112 to "1080P+ 高码率",
    116 to "1080P60 高帧率",
    120 to "4K 超清",
    125 to "HDR 真彩色",
    126 to "杜比视界",
    127 to "8K 超高清",
)


val audioMap = mapOf(
    30216 to "64K",
    30232 to "132K",
    30280 to "192K",
    30250 to "杜比全景声",
    30251 to "Hi-Res无损",
)

object VideoQuality {
    // 备注信息作为常量存储
    const val REMARK_240P = "仅 MP4 格式支持\n仅platform=html5时有效"
    const val REMARK_720P_DEFAULT = "WEB 端默认值\n**无 720P 时则为 720P60**"
    const val REMARK_1080P = "TV 端与 APP 端默认值\n登录认证"
    const val REMARK_1080P_PLUS = "大会员认证"
    const val REMARK_1080P60 = "大会员认证"
    const val REMARK_4K = "需要fnval&128=128且fourk=1\n大会员认证"
    const val REMARK_HDR = "仅支持 DASH 格式\n需要fnval&64=64\n大会员认证"
    const val REMARK_DOLBY = "仅支持 DASH 格式\n需要fnval&512=512\n大会员认证"
    const val REMARK_8K = "仅支持 DASH 格式\n需要fnval&1024=1024\n大会员认证"
}