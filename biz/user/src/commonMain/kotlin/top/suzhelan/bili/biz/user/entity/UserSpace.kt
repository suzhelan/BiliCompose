package top.suzhelan.bili.biz.user.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject


@Serializable
data class UserSpace(
    @SerialName("ad_container_path")
    val adContainerPath: String, // bilibili://mall/smallshop?msource=cps_productTab_479396940&from=cps_productTab_479396940&upmid=479396940
    @SerialName("ad_source_content_v2")
    val adSourceContentV2: AdSourceContentV2,
    @SerialName("archive")
    val archive: Archive,
    @SerialName("article")
    val article: Article,
    @SerialName("audios")
    val audios: Audios,
    @SerialName("card")
    val card: Card,
    @SerialName("cheese")
    val cheese: Cheese,
    @SerialName("coin_archive")
    val coinArchive: CoinArchive,
    @SerialName("comic")
    val comic: Comic,
    @SerialName("default_tab")
    val defaultTab: String, // home
    @SerialName("fans_effect")
    val fansEffect: FansEffect,
    @SerialName("favourite2")
    val favourite2: Favourite2,
    @SerialName("guest_relation")
    val guestRelation: Int, // -999
    @SerialName("images")
    val images: Images,
    @SerialName("like_archive")
    val likeArchive: LikeArchive,
    @SerialName("live")
    val live: Live,
    @SerialName("play_game")
    val playGame: PlayGame,
    @SerialName("relation")
    val relation: Int, // -999
    @SerialName("season")
    val season: Season,
    @SerialName("series")
    val series: Series,
    @SerialName("setting")
    val setting: Setting,
    @SerialName("sub_comic")
    val subComic: SubComic? = null,
    @SerialName("tab")
    val tab: Tab,
    @SerialName("tab2")
    val tab2: List<Tab2>,
    @SerialName("ugc_season")
    val ugcSeason: UgcSeason,
    @SerialName("vip_space_label")
    val vipSpaceLabel: VipSpaceLabel? = null,
) {
    @Serializable
    data class AdSourceContentV2(
        @SerialName("ad_content")
        val adContent: AdContent
    ) {
        @Serializable
        data class AdContent(
            @SerialName("extra")
            val extra: Extra
        ) {
            @Serializable
            data class Extra(
                @SerialName("card")
                val card: Card,
                @SerialName("sales_type")
                val salesType: Int, // 12
                @SerialName("upzone_entrance_report_id")
                val upzoneEntranceReportId: String,
                @SerialName("upzone_entrance_type")
                val upzoneEntranceType: Int // 0
            ) {
                @Serializable
                data class Card(
                    @SerialName("button")
                    val button: Button,
                    @SerialName("card_type")
                    val cardType: Int, // 33
                    @SerialName("covers")
                    val covers: List<Cover>,
                    @SerialName("jump_url")
                    val jumpUrl: String,
                    @SerialName("title")
                    val title: String
                ) {
                    @Serializable
                    data class Button(
                        @SerialName("jump_url")
                        val jumpUrl: String,
                        @SerialName("text")
                        val text: String,
                        @SerialName("type")
                        val type: Int // 1
                    )

                    @Serializable
                    data class Cover(
                        @SerialName("url")
                        val url: String
                    )
                }
            }
        }
    }

    @Serializable
    data class Archive(
        @SerialName("count")
        val count: Int, // 1
        @SerialName("item")
        val item: List<Item>,
        @SerialName("order")
        val order: List<Order>
    ) {
        @Serializable
        data class Item(
            @SerialName("author")
            val author: String, // 临月雨
            @SerialName("bvid")
            val bvid: String, // BV19KWkzQEhD
            @SerialName("cover")
            val cover: String, // http://i0.hdslb.com/bfs/archive/889a4d79267346c925518e774079ae2ce10670ee.jpg
            @SerialName("cover_icon")
            val coverIcon: String,
            @SerialName("cover_left_icon")
            val coverLeftIcon: String,
            @SerialName("cover_left_text")
            val coverLeftText: String,
            @SerialName("ctime")
            val ctime: Int, // 1760669510
            @SerialName("danmaku")
            val danmaku: Int, // 0
            @SerialName("duration")
            val duration: Int, // 93
            @SerialName("first_cid")
            val firstCid: Long, // 33140706101
            @SerialName("goto")
            val goto: String, // av
            @SerialName("icon_type")
            val iconType: Int, // 0
            @SerialName("is_cooperation")
            val isCooperation: Boolean, // false
            @SerialName("is_fold")
            val isFold: Boolean, // false
            @SerialName("is_live_playback")
            val isLivePlayback: Boolean, // false
            @SerialName("is_oneself")
            val isOneself: Boolean, // false
            @SerialName("is_pgc")
            val isPgc: Boolean, // false
            @SerialName("is_popular")
            val isPopular: Boolean, // false
            @SerialName("is_pugv")
            val isPugv: Boolean, // false
            @SerialName("is_steins")
            val isSteins: Boolean, // false
            @SerialName("is_ugcpay")
            val isUgcpay: Boolean, // false
            @SerialName("length")
            val length: String,
            @SerialName("param")
            val `param`: String, // 115387232489222
            @SerialName("play")
            val play: Int, // 0
            @SerialName("publish_time_text")
            val publishTimeText: String, // 3分钟前
            @SerialName("state")
            val state: Boolean, // false
            @SerialName("sub_title_icon")
            val subTitleIcon: String,
            @SerialName("subtitle")
            val subtitle: String,
            @SerialName("title")
            val title: String, // 标题
            @SerialName("tname")
            val tname: String, // 极客DIY
            @SerialName("translate_status")
            val translateStatus: String,
            @SerialName("translated_title")
            val translatedTitle: String,
            @SerialName("ugc_pay")
            val ugcPay: Int, // 0
            @SerialName("uri")
            val uri: String, // bilibili://video/115387232489222?cid=33140706101&history_progress=0&player_height=720&player_preload=%7B%22expire_time%22%3A1760673338%2C%22cid%22%3A33140706101%2C%22quality%22%3A16%2C%22url%22%3A%22http%3A%2F%2Fupos-sz-estghw.bilivideo.com%2Fupgcxcode%2F01%2F61%2F33140706101%2F33140706101-1-16.mp4%3Fe%3Dig8euxZM2rNcNbRVhwdVhwdlhWdVhwdVhoNvNC8BqJIzNbfqXBvEuENvNC8aNEVEtEvE9IMvXBvE2ENvNCImNEVEIj0Y2J_aug859r1qXg8xNEVE5XREto8GuFGv2U7SuxI72X6fTr859IB_%5Cu0026mid%3D479396940%5Cu0026deadline%3D1760676938%5Cu0026uipk%3D5%5Cu0026platform%3Dandroid%5Cu0026trid%3Da00cde71dc2147f3a5bdc372b4887faU%5Cu0026gen%3Dplayurlv3%5Cu0026os%3Destghw%5Cu0026og%3Dhw%5Cu0026nbs%3D1%5Cu0026oi%3D2095235124%5Cu0026upsig%3D8b6c69dd43e87d6b41388fb5526e2c52%5Cu0026uparams%3De%2Cmid%2Cdeadline%2Cuipk%2Cplatform%2Ctrid%2Cgen%2Cos%2Cog%2Cnbs%2Coi%5Cu0026bvc%3Dvod%5Cu0026nettype%3D0%5Cu0026bw%3D106254%5Cu0026agrr%3D0%5Cu0026buvid%3D%5Cu0026build%3D8410300%5Cu0026dl%3D0%5Cu0026f%3DU_0_0%5Cu0026orderid%3D0%2C3%22%2C%22file_info%22%3A%7B%2216%22%3A%7B%22infos%22%3A%5B%7B%22filesize%22%3A1221928%2C%22timelength%22%3A92392%7D%5D%7D%7D%2C%22video_codecid%22%3A7%2C%22video_project%22%3Atrue%2C%22fnver%22%3A0%2C%22fnval%22%3A0%2C%22backup_url%22%3A%5B%22http%3A%2F%2Fupos-sz-mirror08c.bilivideo.com%2Fupgcxcode%2F01%2F61%2F33140706101%2F33140706101-1-16.mp4%3Fe%3Dig8euxZM2rNcNbRVhwdVhwdlhWdVhwdVhoNvNC8BqJIzNbfqXBvEuENvNC8aNEVEtEvE9IMvXBvE2ENvNCImNEVEIj0Y2J_aug859r1qXg8xNEVE5XREto8GuFGv2U7SuxI72X6fTr859IB_%5Cu0026platform%3Dandroid%5Cu0026mid%3D479396940%5Cu0026nbs%3D1%5Cu0026uipk%3D5%5Cu0026trid%3Da00cde71dc2147f3a5bdc372b4887faU%5Cu0026oi%3D2095235124%5Cu0026gen%3Dplayurlv3%5Cu0026os%3D08cbv%5Cu0026og%3Dhw%5Cu0026deadline%3D1760676938%5Cu0026upsig%3Debe2c311395d307d3d28257f124e0fb8%5Cu0026uparams%3De%2Cplatform%2Cmid%2Cnbs%2Cuipk%2Ctrid%2Coi%2Cgen%2Cos%2Cog%2Cdeadline%5Cu0026bvc%3Dvod%5Cu0026nettype%3D0%5Cu0026bw%3D106254%5Cu0026f%3DU_0_0%5Cu0026agrr%3D0%5Cu0026buvid%3D%5Cu0026build%3D8410300%5Cu0026dl%3D0%5Cu0026orderid%3D1%2C3%22%2C%22http%3A%2F%2Fupos-sz-mirrorhwb.bilivideo.com%2Fupgcxcode%2F01%2F61%2F33140706101%2F33140706101-1-16.mp4%3Fe%3Dig8euxZM2rNcNbRVhwdVhwdlhWdVhwdVhoNvNC8BqJIzNbfqXBvEuENvNC8aNEVEtEvE9IMvXBvE2ENvNCImNEVEIj0Y2J_aug859r1qXg8xNEVE5XREto8GuFGv2U7SuxI72X6fTr859IB_%5Cu0026mid%3D479396940%5Cu0026nbs%3D1%5Cu0026uipk%3D5%5Cu0026os%3Dhwbbv%5Cu0026og%3Dhw%5Cu0026deadline%3D1760676938%5Cu0026platform%3Dandroid%5Cu0026trid%3Da00cde71dc2147f3a5bdc372b4887faU%5Cu0026oi%3D2095235124%5Cu0026gen%3Dplayurlv3%5Cu0026upsig%3D13d357d0eb17a1f3891ba4edb130fe2b%5Cu0026uparams%3De%2Cmid%2Cnbs%2Cuipk%2Cos%2Cog%2Cdeadline%2Cplatform%2Ctrid%2Coi%2Cgen%5Cu0026bvc%3Dvod%5Cu0026nettype%3D0%5Cu0026bw%3D106254%5Cu0026agrr%3D0%5Cu0026buvid%3D%5Cu0026build%3D8410300%5Cu0026dl%3D0%5Cu0026f%3DU_0_0%5Cu0026orderid%3D2%2C3%22%5D%2C%22accept_formats%22%3A%5B%7B%22quality%22%3A64%2C%22format%22%3A%22mp4720%22%2C%22description%22%3A%22%E9%AB%98%E6%B8%85%20720P%22%2C%22new_description%22%3A%22720P%20%E5%87%86%E9%AB%98%E6%B8%85%22%2C%22display_desc%22%3A%22720P%22%2C%22need_login%22%3Atrue%7D%2C%7B%22quality%22%3A16%2C%22format%22%3A%22mp4%22%2C%22description%22%3A%22%E6%B5%81%E7%95%85%20360P%22%2C%22new_description%22%3A%22360P%20%E6%B5%81%E7%95%85%22%2C%22display_desc%22%3A%22360P%22%7D%5D%2C%22union_player%22%3A%7B%22biz_type%22%3A1%2C%22dimension%22%3A%7B%22width%22%3A1280%2C%22height%22%3A720%2C%22rotate%22%3A0%7D%2C%22aid%22%3A115387232489222%7D%7D&player_rotate=0&player_width=1280
            @SerialName("videos")
            val videos: Int, // 1
            @SerialName("view_content")
            val viewContent: String, // 0
            @SerialName("view_self_type")
            val viewSelfType: Int // 0
        )

        @Serializable
        data class Order(
            @SerialName("title")
            val title: String, // 最新发布
            @SerialName("value")
            val value: String // pubdate
        )
    }

    @Serializable
    data class Article(
        @SerialName("count")
        val count: Int, // 0
        @SerialName("item")
        val item: List<JsonObject?>,//作用未知
        @SerialName("lists")
        val lists: List<JsonObject?>,//作用未知
        @SerialName("lists_count")
        val listsCount: Int // 0
    )

    @Serializable
    data class Audios(
        @SerialName("count")
        val count: Int, // 0
        @SerialName("item")
        val item: List<JsonObject?>//推测是音乐相关的东西
    )

    @Serializable
    data class Card(
        @SerialName("approve")
        val approve: Boolean, // false
        @SerialName("article")
        val article: Int, // 0
        @SerialName("attention")
        val attention: Int, // 24
        @SerialName("birthday")
        val birthday: String,
        @SerialName("description")
        val description: String,
        @SerialName("digital_id")
        val digitalId: String,
        @SerialName("digital_type")
        val digitalType: Int, // -2
        @SerialName("DisplayRank")
        val displayRank: String,
        @SerialName("end_time")
        val endTime: Int, // 0
        @SerialName("entrance")
        val entrance: Entrance,
        @SerialName("face")
        val face: String, // https://i0.hdslb.com/bfs/face/c45d94eeedc3e2c2b38129f94a6ca4fc92c58c85.jpg
        @SerialName("face_nft_new")
        val faceNftNew: Int, // 0
        @SerialName("fans")
        val fans: Int, // 3
        @SerialName("friend")
        val friend: Int, // 0
        @SerialName("has_digital_asset")
        val hasDigitalAsset: Boolean, // false
        @SerialName("has_face_nft")
        val hasFaceNft: Boolean, // false
        @SerialName("honours")
        val honours: Honours,
        @SerialName("is_deleted")
        val isDeleted: Int, // 0
        @SerialName("level_info")
        val levelInfo: LevelInfo,
        @SerialName("likes")
        val likes: Likes,
        @SerialName("live_fans_wearing")
        val liveFansWearing: LiveFansWearing? = null,
        @SerialName("mid")
        val mid: String, // 479396940
        @SerialName("name")
        val name: String, // 临月雨
        @SerialName("nameplate")
        val nameplate: Nameplate,
        @SerialName("nft_id")
        val nftId: String,
        @SerialName("official_verify")
        val officialVerify: OfficialVerify,
        @SerialName("pendant")
        val pendant: Pendant,
        @SerialName("pendant_title")
        val pendantTitle: String? = null, // 更换头像挂件
        @SerialName("pendant_url")
        val pendantUrl: String? = null, // https://www.bilibili.com/h5/mall/pendant/home?navhide=1&from=personal_space
        @SerialName("place")
        val place: String,
        @SerialName("profession_verify")
        val professionVerify: ProfessionVerify,
        @SerialName("rank")
        val rank: String,
        @SerialName("regtime")
        val regtime: Int, // 0
        @SerialName("relation")
        val relation: Relation,
        @SerialName("sign")
        val sign: String, // ?
        @SerialName("silence")
        val silence: Int, // 0
        @SerialName("silence_url")
        val silenceUrl: String,
        @SerialName("space_tag")
        val spaceTag: List<SpaceTag>,
        @SerialName("spacesta")
        val spacesta: Int, // 0
        @SerialName("vip")
        val vip: Vip
    ) {


        @Serializable
        data class Entrance(
            @SerialName("icon")
            val icon: String, // https://i0.hdslb.com/bfs/activity-plat/static/2be2c5f696186bad80d4b452e4af2a76/qGbJfq9VGe.png
            @SerialName("is_show_entrance")
            val isShowEntrance: Boolean, // false
            @SerialName("jump_url")
            val jumpUrl: String
        )

        @Serializable
        data class Honours(
            @SerialName("colour")
            val colour: Colour,
            @SerialName("tags")
            val tags: List<JsonObject?>//作用未知
        ) {
            @Serializable
            data class Colour(
                @SerialName("dark")
                val dark: String, // #CE8620
                @SerialName("normal")
                val normal: String // #F0900B
            )
        }

        @Serializable
        data class LevelInfo(
            @SerialName("current_exp")
            val currentExp: Int, // 10565
            @SerialName("current_level")
            val currentLevel: Int, // 4
            @SerialName("current_min")
            val currentMin: Int, // 4500
            @SerialName("identity")
            val identity: Int, // 0
            @SerialName("next_exp")
            val nextExp: Int? = null, // 10800,自己的有值 别人的为空
            @SerialName("senior_inquiry")
            val seniorInquiry: SeniorInquiry
        ) {
            @Serializable
            data class SeniorInquiry(
                @SerialName("inquiry_text")
                val inquiryText: String,
                @SerialName("inquiry_url")
                val inquiryUrl: String
            )
        }

        @Serializable
        data class Likes(
            @SerialName("like_num")
            val likeNum: Int, // 3
            @SerialName("skr_tip")
            val skrTip: String // 视频、专栏、动态累计获赞
        )

        @Serializable
        data class LiveFansWearing(
            @SerialName("detail_v2")
            val detailV2: JsonObject?, // 作用未知
            @SerialName("medal_jump_url")
            val medalJumpUrl: String, // https://live.bilibili.com/p/html/live-fansmedal-wall/index.html?is_live_webview=1&tId=479396940#/medal
            @SerialName("show_default_icon")
            val showDefaultIcon: Boolean = false // 提供默认值
        )


        @Serializable
        data class Nameplate(
            @SerialName("condition")
            val condition: String,
            @SerialName("image")
            val image: String,
            @SerialName("image_small")
            val imageSmall: String,
            @SerialName("level")
            val level: String,
            @SerialName("name")
            val name: String,
            @SerialName("nid")
            val nid: Int // 0
        )

        @Serializable
        data class OfficialVerify(
            @SerialName("desc")
            val desc: String,
            @SerialName("icon")
            val icon: String,
            @SerialName("role")
            val role: Int, // 0
            @SerialName("splice_title")
            val spliceTitle: String,
            @SerialName("title")
            val title: String,
            @SerialName("type")
            val type: Int // -1
        )

        @Serializable
        data class Pendant(
            @SerialName("expire")
            val expire: Int, // 0
            @SerialName("image")
            val image: String,
            @SerialName("image_enhance")
            val imageEnhance: String,
            @SerialName("image_enhance_frame")
            val imageEnhanceFrame: String,
            @SerialName("n_pid")
            val nPid: Int, // 0
            @SerialName("name")
            val name: String,
            @SerialName("pid")
            val pid: Int // 0
        )

        @Serializable
        data class ProfessionVerify(
            @SerialName("icon")
            val icon: String,
            @SerialName("show_desc")
            val showDesc: String
        )

        @Serializable
        data class Relation(
            @SerialName("status")
            val status: Int // 1
        )


        @Serializable
        data class SpaceTag(
            @SerialName("background_color")
            val backgroundColor: String,
            @SerialName("icon")
            val icon: String, // https://i0.hdslb.com/bfs/activity-plat/static/2be2c5f696186bad80d4b452e4af2a76/fAILMRg9PS.png
            @SerialName("night_background_color")
            val nightBackgroundColor: String,
            @SerialName("night_text_color")
            val nightTextColor: String, // #A2A7AE
            @SerialName("text_color")
            val textColor: String, // #61666D
            @SerialName("title")
            val title: String, // IP属地：广西
            @SerialName("type")
            val type: String, // location
            @SerialName("uri")
            val uri: String
        )

        @Serializable
        data class Vip(
            @SerialName("accessStatus")
            val accessStatus: Int, // 0
            @SerialName("dueRemark")
            val dueRemark: String,
            @SerialName("label")
            val label: Label,
            @SerialName("themeType")
            val themeType: Int, // 0
            @SerialName("vipDueDate")
            val vipDueDate: Long, // 1664726400000 到期时间 时间戳 毫秒
            @SerialName("vipStatus")
            val vipStatus: Int, // 0：无<br />1：有
            @SerialName("vipStatusWarn")
            val vipStatusWarn: String,
            @SerialName("vipType")
            val vipType: Int // 0：无<br />1：月度大会员<br />2：年度以上大会员
        ) {
            @Serializable
            data class Label(
                @SerialName("bg_color")
                val bgColor: String,
                @SerialName("bg_style")
                val bgStyle: Int, // 1
                @SerialName("border_color")
                val borderColor: String,
                @SerialName("image")
                val image: String, // https://i0.hdslb.com/bfs/vip/d7b702ef65a976b20ed854cbd04cb9e27341bb79.png
                @SerialName("label_goto")
                val labelGoto: String,
                @SerialName("label_id")
                val labelId: Int, // 0
                @SerialName("label_theme")
                val labelTheme: String,
                @SerialName("path")
                val path: String, // https://i0.hdslb.com/bfs/vip/label_overdue.png
                @SerialName("text")
                val text: String,
                @SerialName("text_color")
                val textColor: String
            )
        }
    }

    @Serializable
    data class Cheese(
        @SerialName("count")
        val count: Int, // 0
        @SerialName("item")
        val item: List<JsonObject?>//作用未知
    )

    @Serializable
    data class CoinArchive(
        @SerialName("count")
        val count: Int, // 2
        @SerialName("item")
        val item: List<Item>
    ) {
        @Serializable
        data class Item(
            @SerialName("author")
            val author: String, // 一番星baby
            @SerialName("cover")
            val cover: String, // http://i0.hdslb.com/bfs/archive/c8a2bfab0817aa7af5ba0213275e4d7aa41dea96.jpg
            @SerialName("cover_icon")
            val coverIcon: String,
            @SerialName("cover_left_icon")
            val coverLeftIcon: String,
            @SerialName("cover_left_text")
            val coverLeftText: String,
            @SerialName("ctime")
            val ctime: Int, // 1757221709
            @SerialName("danmaku")
            val danmaku: Int, // 1
            @SerialName("duration")
            val duration: Int, // 17
            @SerialName("goto")
            val goto: String, // av
            @SerialName("icon_type")
            val iconType: Int, // 0
            @SerialName("is_cooperation")
            val isCooperation: Boolean, // false
            @SerialName("is_fold")
            val isFold: Boolean, // false
            @SerialName("is_live_playback")
            val isLivePlayback: Boolean, // false
            @SerialName("is_oneself")
            val isOneself: Boolean, // false
            @SerialName("is_pgc")
            val isPgc: Boolean, // false
            @SerialName("is_popular")
            val isPopular: Boolean, // false
            @SerialName("is_pugv")
            val isPugv: Boolean, // false
            @SerialName("is_steins")
            val isSteins: Boolean, // false
            @SerialName("is_ugcpay")
            val isUgcpay: Boolean, // false
            @SerialName("length")
            val length: String,
            @SerialName("param")
            val `param`: String, // 115161276882442
            @SerialName("play")
            val play: Int, // 45729
            @SerialName("publish_time_text")
            val publishTimeText: String,
            @SerialName("state")
            val state: Boolean, // true
            @SerialName("sub_title_icon")
            val subTitleIcon: String,
            @SerialName("subtitle")
            val subtitle: String,
            @SerialName("title")
            val title: String, // 我们两只小猫其中有一只很萌๑ ᳐>⩊< ᳐๑
            @SerialName("tname")
            val tname: String,
            @SerialName("translate_status")
            val translateStatus: String,
            @SerialName("translated_title")
            val translatedTitle: String,
            @SerialName("ugc_pay")
            val ugcPay: Int, // 0
            @SerialName("uri")
            val uri: String, // bilibili://video/115161276882442?player_width=2160&player_height=2160&player_rotate=0&biz_type=1
            @SerialName("videos")
            val videos: Int, // 0
            @SerialName("view_content")
            val viewContent: String, // 4.6万
            @SerialName("view_self_type")
            val viewSelfType: Int // 0
        )
    }

    @Serializable
    data class Comic(
        @SerialName("count")
        val count: Int, // 0
        @SerialName("item")
        val item: List<JsonObject?>//估计是漫画相关业务
    )


    @Serializable
    class FansEffect

    @Serializable
    data class Favourite2(
        @SerialName("count")
        val count: Int, // 4
        @SerialName("item")
        val item: List<Item>
    ) {
        @Serializable
        data class Item(
            @SerialName("count")
            val count: Int, // 2
            @SerialName("cover")
            val cover: String, // http://i2.hdslb.com/bfs/archive/6a78b832220b680950dc9f6fef8202dc7e7a0cef.jpg
            @SerialName("ctime")
            val ctime: Int, // 1572268317
            @SerialName("id")
            val id: Int, // 7512743
            @SerialName("is_public")
            val isPublic: Int, // 0
            @SerialName("media_id")
            val mediaId: Long, // 3368321940
            @SerialName("mid")
            val mid: Long, // 479396940
            @SerialName("mtime")
            val mtime: Int, // 1736516997
            @SerialName("title")
            val title: String, // 默认收藏夹
            @SerialName("type")
            val type: Int // 2
        )
    }

    @Serializable
    data class Images(
        @SerialName("collection_top_simple")
        val collectionTopSimple: CollectionTopSimple,
        @SerialName("digital_info")
        val digitalInfo: DigitalInfo,
        @SerialName("entrance_button")
        val entranceButton: EntranceButton,
        @SerialName("goods_available")
        val goodsAvailable: Boolean, // true
        @SerialName("imgUrl")
        val imgUrl: String, // https://i0.hdslb.com/bfs/app/db6ce3669d04a08342120874edea7521562da6a3.png
        @SerialName("night_imgurl")
        val nightImgurl: String,
        @SerialName("purchase_button")
        val purchaseButton: PurchaseButton,
    ) {
        @Serializable
        data class CollectionTopSimple(
            @SerialName("collection_completed_url")
            val collectionCompletedUrl: String,
            @SerialName("max")
            val max: Int, // 0
            @SerialName("preference")
            val preference: JsonObject?, // 未知 可能是什么偏好
            @SerialName("top")
            val top: JsonObject? // 未知
        )

        @Serializable
        data class DigitalInfo(
            @SerialName("active")
            val active: Boolean, // false
            @SerialName("animation")
            val animation: JsonObject?, // 动漫?
            @SerialName("animation_first_frame")
            val animationFirstFrame: String,
            @SerialName("background_handle")
            val backgroundHandle: Int, // 0
            @SerialName("card_id")
            val cardId: Int, // 0
            @SerialName("cut_space_bg")
            val cutSpaceBg: String,
            @SerialName("item_jump_url")
            val itemJumpUrl: String,
            @SerialName("music_album")
            val musicAlbum: JsonObject?, // 音乐专辑? 我不知道
            @SerialName("nft_region_title")
            val nftRegionTitle: String,
            @SerialName("nft_type")
            val nftType: Int, // 0
            @SerialName("part_type")
            val partType: Int // 0
        )

        @Serializable
        data class EntranceButton(
            @SerialName("title")
            val title: String, // 个性装扮IP粉丝头图
            @SerialName("uri")
            val uri: String // https://www.bilibili.com/h5/mall/v2/spaceCollectionSetting?navhide=1
        )

        @Serializable
        data class PurchaseButton(
            @SerialName("title")
            val title: String, // 去购买
            @SerialName("uri")
            val uri: String // http://www.bilibili.com/h5/mall/home?navhide=1&from=personal_space
        )
    }

    @Serializable
    data class LikeArchive(
        @SerialName("count")
        val count: Int, // 600
        @SerialName("item")
        val item: List<Item>
    ) {
        @Serializable
        data class Item(
            @SerialName("author")
            val author: String, // o卷糕喵喵o
            @SerialName("cover")
            val cover: String, // http://i0.hdslb.com/bfs/archive/a4c8322391b39ee026013826d118a6696d572d30.jpg
            @SerialName("cover_icon")
            val coverIcon: String,
            @SerialName("cover_left_icon")
            val coverLeftIcon: String,
            @SerialName("cover_left_text")
            val coverLeftText: String,
            @SerialName("ctime")
            val ctime: Int, // 1760352934
            @SerialName("danmaku")
            val danmaku: Int, // 0
            @SerialName("duration")
            val duration: Int, // 13
            @SerialName("goto")
            val goto: String, // av
            @SerialName("icon_type")
            val iconType: Int, // 0
            @SerialName("is_cooperation")
            val isCooperation: Boolean, // false
            @SerialName("is_fold")
            val isFold: Boolean, // false
            @SerialName("is_live_playback")
            val isLivePlayback: Boolean, // false
            @SerialName("is_oneself")
            val isOneself: Boolean, // false
            @SerialName("is_pgc")
            val isPgc: Boolean, // false
            @SerialName("is_popular")
            val isPopular: Boolean, // false
            @SerialName("is_pugv")
            val isPugv: Boolean, // false
            @SerialName("is_steins")
            val isSteins: Boolean, // false
            @SerialName("is_ugcpay")
            val isUgcpay: Boolean, // false
            @SerialName("length")
            val length: String,
            @SerialName("param")
            val `param`: String, // 115366479069338
            @SerialName("play")
            val play: Int, // 3242
            @SerialName("publish_time_text")
            val publishTimeText: String,
            @SerialName("state")
            val state: Boolean, // true
            @SerialName("sub_title_icon")
            val subTitleIcon: String,
            @SerialName("subtitle")
            val subtitle: String,
            @SerialName("title")
            val title: String, // 像老实人豁出去了
            @SerialName("tname")
            val tname: String,
            @SerialName("translate_status")
            val translateStatus: String,
            @SerialName("translated_title")
            val translatedTitle: String,
            @SerialName("ugc_pay")
            val ugcPay: Int, // 0
            @SerialName("uri")
            val uri: String, // bilibili://video/115366479069338?player_width=1080&player_height=1920&player_rotate=0&biz_type=1
            @SerialName("videos")
            val videos: Int, // 0
            @SerialName("view_content")
            val viewContent: String, // 3242
            @SerialName("view_self_type")
            val viewSelfType: Int // 0
        )
    }

    @Serializable
    data class Live(
        @SerialName("broadcast_type")
        val broadcastType: Int, // 0
        @SerialName("cover")
        val cover: String,
        @SerialName("link")
        val link: String,
        @SerialName("liveStatus")
        val liveStatus: Int, // 0
        @SerialName("online")
        val online: Int, // 0
        @SerialName("online_hidden")
        val onlineHidden: Int, // 0
        @SerialName("roomStatus")
        val roomStatus: Int, // 0
        @SerialName("roomid")
        val roomid: Long, // 0
        @SerialName("roundStatus")
        val roundStatus: Int, // 0
        @SerialName("title")
        val title: String,
        @SerialName("url")
        val url: String
    )

    @Serializable
    data class PlayGame(
        @SerialName("count")
        val count: Int, // 2
        @SerialName("item")
        val item: List<Item>
    ) {
        @Serializable
        data class Item(
            @SerialName("grade")
            val grade: Double, // 8.9
            @SerialName("icon")
            val icon: String, // https://i0.hdslb.com/bfs/game/7fbcbaaf18ebc11de1d8241ec4d82521024634ed.jpg
            @SerialName("id")
            val id: Int, // 111964
            @SerialName("name")
            val name: String, // 雪松
            @SerialName("tag")
            val tag: List<String>,
            @SerialName("uri")
            val uri: String // bilibili://game_center/detail?id=111964&sourceFrom=666
        )
    }

    @Serializable
    data class Season(
        @SerialName("count")
        val count: Int, // 1
        @SerialName("item")
        val item: List<Item>
    ) {
        @Serializable
        data class Item(
            @SerialName("attention")
            val attention: String, // 1
            @SerialName("cover")
            val cover: String, // http://i0.hdslb.com/bfs/bangumi/2e4e045710b932a63a03a9d4505bda9f49e7525b.jpg
            @SerialName("finish")
            val finish: Int, // 1
            @SerialName("goto")
            val goto: String, // bangumi
            @SerialName("index")
            val index: String,
            @SerialName("is_finish")
            val isFinish: String,
            @SerialName("is_started")
            val isStarted: Int, // 1
            @SerialName("mtime")
            val mtime: Int, // 0
            @SerialName("newest_ep_id")
            val newestEpId: String,
            @SerialName("newest_ep_index")
            val newestEpIndex: String, // 8
            @SerialName("param")
            val `param`: String, // 660
            @SerialName("title")
            val title: String, // 十万个冷笑话 第一季
            @SerialName("total_count")
            val totalCount: String, // 8
            @SerialName("uri")
            val uri: String // http://bangumi.bilibili.com/anime/660
        )
    }

    @Serializable
    data class Series(
        @SerialName("item")
        val item: List<JsonObject?>//未知
    )

    @Serializable
    data class Setting(
        @SerialName("bangumi")
        val bangumi: Int, // 0
        @SerialName("bbq")
        val bbq: Int, // 0
        @SerialName("channel")
        val channel: Int? = null, // 1
        @SerialName("charge_video")
        val chargeVideo: Int, // 0
        @SerialName("close_space_medal")
        val closeSpaceMedal: Int, // 1
        @SerialName("coins_video")
        val coinsVideo: Int, // 0
        @SerialName("comic")
        val comic: Int, // 0
        @SerialName("disable_following")
        val disableFollowing: Int, // 1
        @SerialName("disable_show_fans")
        val disableShowFans: Int, // 1
        @SerialName("disable_show_nft")
        val disableShowNft: Int, // 0
        @SerialName("disable_show_school")
        val disableShowSchool: Int, // 1
        @SerialName("dress_up")
        val dressUp: Int, // 0
        @SerialName("fav_video")
        val favVideo: Int, // 0
        @SerialName("groups")
        val groups: Int, // 0
        @SerialName("lesson_video")
        val lessonVideo: Int, // 0
        @SerialName("likes_video")
        val likesVideo: Int, // 0
        @SerialName("live_playback")
        val livePlayback: Int, // 0
        @SerialName("only_show_wearing")
        val onlyShowWearing: Int, // 0
        @SerialName("played_game")
        val playedGame: Int // 0
    )

    @Serializable
    data class SubComic(
        @SerialName("count")
        val count: Int, // 0
        @SerialName("item")
        val item: List<JsonObject?>//子漫画?
    )

    @Serializable
    data class Tab(
        @SerialName("activity")
        val activity: Boolean, // false
        @SerialName("album")
        val album: Boolean, // false
        @SerialName("archive")
        val archive: Boolean, // true
        @SerialName("article")
        val article: Boolean, // false
        @SerialName("audios")
        val audios: Boolean, // false
        @SerialName("bangumi")
        val bangumi: Boolean, // true
        @SerialName("brand")
        val brand: Boolean, // false
        @SerialName("charging")
        val charging: Boolean, // false
        @SerialName("cheese")
        val cheese: Boolean, // false
        @SerialName("cheese_video")
        val cheeseVideo: Boolean, // false
        @SerialName("clip")
        val clip: Boolean, // false
        @SerialName("coin")
        val coin: Boolean, // true
        @SerialName("comic")
        val comic: Boolean, // false
        @SerialName("community")
        val community: Boolean, // false
        @SerialName("dynamic")
        val `dynamic`: Boolean, // true
        @SerialName("favorite")
        val favorite: Boolean, // true
        @SerialName("like")
        val like: Boolean, // true
        @SerialName("mall")
        val mall: Boolean, // false
        @SerialName("opus")
        val opus: Boolean, // false
        @SerialName("series")
        val series: Boolean, // false
        @SerialName("shop")
        val shop: Boolean, // false
        @SerialName("sub_comic")
        val subComic: Boolean, // false
        @SerialName("ugc_season")
        val ugcSeason: Boolean // false
    )

    @Serializable
    data class Tab2(
        @SerialName("items")
        val items: List<Item> = emptyList(),
        @SerialName("param")
        val `param`: String, // home
        @SerialName("title")
        val title: String // 主页,动态,投稿...
    ) {
        /**
         * 为投稿时出现的子项
         */
        @Serializable
        data class Item(
            @SerialName("param")
            val param: String, // video
            @SerialName("title")
            val title: String // 视频
        )
    }

    @Serializable
    data class UgcSeason(
        @SerialName("count")
        val count: Int, // 0
        @SerialName("item")
        val item: List<JsonObject?>//完全未知
    )

    @Serializable
    data class VipSpaceLabel(
        @SerialName("show_expire")
        val showExpire: Boolean // false
    )
}



