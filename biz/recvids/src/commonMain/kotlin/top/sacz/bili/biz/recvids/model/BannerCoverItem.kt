package top.sacz.bili.biz.recvids.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

inline val BannerCoverItem.Companion.targetCardType get() = "banner_v8"

fun BannerCoverItem.BannerItem.isAd(): Boolean {
    return type == "ad"
}

fun BannerCoverItem.BannerItem.isStatic(): Boolean {
    return type == "static"
}

fun BannerCoverItem.BannerItem.isAv(): Boolean {
    return !isAd() && !isStatic()
}

//TODO 还没做 先别用 banner的类型和处理有点复杂
@Serializable
data class BannerCoverItem(
    @SerialName("card_goto") override val cardGoto: String = "",
    @SerialName("card_type") override val cardType: String = "",
    @SerialName("idx") override val idx: Int = 0,
    @SerialName("args")
    val args: Args,
    @SerialName("banner_item")
    val bannerItem: List<BannerItem>,
    @SerialName("hash")
    val hash: String,
    @SerialName("track_id")
    val trackId: String
) : BaseCoverItem() {
    @Serializable
    data class BannerItem(
        @SerialName("ad_banner") val adBanner: AdBanner,
        @SerialName("args") val args: ArgsX,
        @SerialName("can_play") val canPlay: Int,
        @SerialName("card_goto") val cardGoto: String,
        @SerialName("card_type") val cardType: String,
        @SerialName("cover") val cover: String,
        @SerialName("cover_info_priority") val coverInfoPriority: Int,
        @SerialName("cover_left_1_content_description") val coverLeft1ContentDescription: String,
        @SerialName("cover_left_2_content_description") val coverLeft2ContentDescription: String,
        @SerialName("cover_left_icon_1") val coverLeftIcon1: Int,
        @SerialName("cover_left_icon_2") val coverLeftIcon2: Int,
        @SerialName("cover_left_text_1") val coverLeftText1: String,
        @SerialName("cover_left_text_2") val coverLeftText2: String,
        @SerialName("cover_right_content_description") val coverRightContentDescription: String,
        @SerialName("cover_right_text") val coverRightText: String,
        @SerialName("desc_button") val descButton: DescButton,
        @SerialName("goto") val goto: String,
        @SerialName("goto_icon") val gotoIcon: GotoIcon,
        @SerialName("id") val id: Int,
        @SerialName("idx") val idx: Int,
        @SerialName("index") val index: Int,
        @SerialName("official_icon") val officialIcon: Int,
        @SerialName("param") val param: String,
        @SerialName("player_args") val playerArgs: PlayerArgs,
        @SerialName("report_flow_data") val reportFlowData: String,
        @SerialName("resource_id") val resourceId: Int,
        @SerialName("static_banner") val staticBanner: StaticBanner,
        @SerialName("talk_back") val talkBack: String,
        @SerialName("three_point") val threePoint: ThreePoint,
        @SerialName("three_point_v") val threePointV: String,
        @SerialName("three_point_v2") val threePointV2: List<ThreePointV2>,
        @SerialName("title") val title: String,
        @SerialName("track_id") val trackId: String,
        @SerialName("type") val type: String,
        @SerialName("uri") val uri: String
    )

    @Serializable
    data class AdBanner(
        @SerialName("ad_cb") val adCb: String,
        @SerialName("client_ip") val clientIp: String,
        @SerialName("cm_mark") val cmMark: Int,
        @SerialName("creative_id") val creativeId: Long,
        @SerialName("extra") val extra: Extra,
        @SerialName("hash") val hash: String,
        @SerialName("id") val id: Int,
        @SerialName("image") val image: String,
        @SerialName("index") val index: Int,
        @SerialName("is_ad") val isAd: Boolean,
        @SerialName("is_ad_loc") val isAdLoc: Boolean,
        @SerialName("request_id") val requestId: String,
        @SerialName("resource_id") val resourceId: Int,
        @SerialName("server_type") val serverType: Int,
        @SerialName("src_id") val srcId: Int,
        @SerialName("title") val title: String,
        @SerialName("uri") val uri: String
    )

    @Serializable
    data class ArgsX(
        @SerialName("aid") val aid: Long,
        @SerialName("tid") val tid: Int,
        @SerialName("tname") val tname: String,
        @SerialName("up_id") val upId: Int,
        @SerialName("up_name") val upName: String
    )

    @Serializable
    data class StaticBanner(
        @SerialName("client_ip") val clientIp: String,
        @SerialName("cm_mark") val cmMark: Int,
        @SerialName("hash") val hash: String,
        @SerialName("id") val id: Int,
        @SerialName("image") val image: String,
        @SerialName("index") val index: Int,
        @SerialName("is_ad_loc") val isAdLoc: Boolean,
        @SerialName("request_id") val requestId: String,
        @SerialName("resource_id") val resourceId: Int,
        @SerialName("server_type") val serverType: Int,
        @SerialName("src_id") val srcId: Int
    )

    @Serializable
    data class Extra(
        @SerialName("ad_content_type") val adContentType: Int,
        @SerialName("app_exp_params") val appExpParams: String,
        @SerialName("card") val card: Card,
        @SerialName("click_area") val clickArea: Int,
//    @SerialName("click_urls") val clickUrls: List<Any>,
        @SerialName("comment_biz_type") val commentBizType: Int,
        @SerialName("comment_toast_open") val commentToastOpen: Int,
        @SerialName("download_url_type") val downloadUrlType: Int,
//    @SerialName("download_whitelist") val downloadWhitelist: List<Any>,
        @SerialName("enable_auto_callup") val enableAutoCallup: Int,
        @SerialName("enable_double_jump") val enableDoubleJump: Boolean,
        @SerialName("enable_h5_alert") val enableH5Alert: Boolean,
        @SerialName("enable_h5_pre_load") val enableH5PreLoad: Int,
        @SerialName("enable_openapk_dialog") val enableOpenapkDialog: Boolean,
        @SerialName("enable_share") val enableShare: Boolean,
        @SerialName("enable_store_direct_launch") val enableStoreDirectLaunch: Int,
        @SerialName("feedback_panel_style") val feedbackPanelStyle: Int,
        @SerialName("from_track_id") val fromTrackId: String,
        @SerialName("hot_activity_id") val hotActivityId: Int,
        @SerialName("landingpage_download_style") val landingpageDownloadStyle: Int,
        @SerialName("lottery_id") val lotteryId: Int,
        @SerialName("macro_replace_priority") val macroReplacePriority: Int,
//    @SerialName("middle_click_urls") val middleClickUrls: List<Any>,
//    @SerialName("middle_show_urls") val middleShowUrls: List<Any>,
        @SerialName("preload_landingpage") val preloadLandingpage: Int,
        @SerialName("product_id") val productId: Int,
        @SerialName("report_time") val reportTime: Int,
        @SerialName("sales_type") val salesType: Int,
        @SerialName("share_info") val shareInfo: ShareInfo,
        @SerialName("shop_id") val shopId: Int,
        @SerialName("show_urls") val showUrls: List<String>,
        @SerialName("special_industry") val specialIndustry: Boolean,
        @SerialName("special_industry_style") val specialIndustryStyle: Int,
        @SerialName("store_callup_card") val storeCallupCard: Boolean,
        @SerialName("top_live_stay_time_seconds") val topLiveStayTimeSeconds: Int,
        @SerialName("track_id") val trackId: String,
        @SerialName("up_mid") val upMid: Int,
        @SerialName("upzone_entrance_report_id") val upzoneEntranceReportId: Int,
        @SerialName("upzone_entrance_type") val upzoneEntranceType: Int,
        @SerialName("use_ad_web_v2") val useAdWebV2: Boolean,
        @SerialName("user_cancel_jump_type") val userCancelJumpType: Int,
        @SerialName("vipshop_fast_framework") val vipshopFastFramework: Int
    )

    @Serializable
    data class Card(
        @SerialName("ad_tag") val adTag: String,
        @SerialName("ad_tag_style") val adTagStyle: AdTagStyle,
        @SerialName("adver") val adver: Adver,
        @SerialName("anim_in_enable") val animInEnable: Int,
        @SerialName("callup_url") val callupUrl: String,
        @SerialName("card_type") val cardType: Int,
//    @SerialName("choose_button_list") val chooseButtonList: List<Any>,
        @SerialName("closed_loop_item") val closedLoopItem: Int,
        @SerialName("comment_use_game_page") val commentUseGamePage: Int,
        @SerialName("covers") val covers: List<Cover>,
//    @SerialName("custom_feedback_panels") val customFeedbackPanels: List<Any>,
        @SerialName("desc") val desc: String,
        @SerialName("desc_type") val descType: Int,
        @SerialName("download_area") val downloadArea: Int,
        @SerialName("duration") val duration: String,
        @SerialName("dynamic_text") val dynamicText: String,
        @SerialName("enable_tag_move_up") val enableTagMoveUp: Int,
        @SerialName("extra_desc") val extraDesc: String,
        @SerialName("extreme_team_status") val extremeTeamStatus: Boolean,
        @SerialName("feedback_panel") val feedbackPanel: FeedbackPanel,
        @SerialName("fold_time") val foldTime: Int,
        @SerialName("goods_item_id") val goodsItemId: Int,
        @SerialName("goods_panel_show") val goodsPanelShow: Int,
        @SerialName("goods_pannel_show") val goodsPannelShow: Int,
        @SerialName("grade_denominator") val gradeDenominator: Int,
        @SerialName("grade_level") val gradeLevel: Int,
        @SerialName("item_source") val itemSource: Int,
        @SerialName("jump_interaction_style") val jumpInteractionStyle: Int,
        @SerialName("jump_url") val jumpUrl: String,
        @SerialName("live_auto_play") val liveAutoPlay: Boolean,
        @SerialName("live_booking_population_threshold") val liveBookingPopulationThreshold: Int,
        @SerialName("live_card_show") val liveCardShow: Boolean,
        @SerialName("live_page_type") val livePageType: Int,
        @SerialName("live_room_popularity") val liveRoomPopularity: Int,
        @SerialName("live_tag_show") val liveTagShow: Boolean,
        @SerialName("long_desc") val longDesc: String,
        @SerialName("ori_mark_hidden") val oriMarkHidden: Int,
        @SerialName("original_style_level") val originalStyleLevel: Int,
        @SerialName("playpage_card_style") val playpageCardStyle: Int,
//    @SerialName("quality_infos") val qualityInfos: List<Any>,
        @SerialName("referral_pop_active_time") val referralPopActiveTime: Int,
        @SerialName("search_show_adbutton") val searchShowAdbutton: Int,
        @SerialName("show_pop_window") val showPopWindow: Int,
        @SerialName("star_level") val starLevel: Int,
        @SerialName("story_interaction_style") val storyInteractionStyle: Int,
        @SerialName("story_takeoff_interaction_style") val storyTakeoffInteractionStyle: Int,
        @SerialName("support_transition") val supportTransition: Boolean,
        @SerialName("title") val title: String,
        @SerialName("under_player_interaction_style") val underPlayerInteractionStyle: Int,
        @SerialName("underframe_card_style") val underframeCardStyle: Int,
        @SerialName("universal_app") val universalApp: String,
        @SerialName("use_multi_cover") val useMultiCover: Boolean,
//    @SerialName("videos") val videos: List<Any>,
        @SerialName("yellow_cart_pannel_pullup") val yellowCartPannelPullup: Int,
        @SerialName("yellow_cart_pannel_version") val yellowCartPannelVersion: Int
    )

    @Serializable
    data class ShareInfo(
        @SerialName("image_url") val imageUrl: String,
        @SerialName("subtitle") val subtitle: String,
        @SerialName("title") val title: String
    )

    @Serializable
    data class AdTagStyle(
        @SerialName("bg_border_color") val bgBorderColor: String,
        @SerialName("bg_color") val bgColor: String,
        @SerialName("border_color") val borderColor: String,
        @SerialName("img_height") val imgHeight: Int,
        @SerialName("img_url") val imgUrl: String,
        @SerialName("img_width") val imgWidth: Int,
        @SerialName("text") val text: String,
        @SerialName("text_color") val textColor: String,
        @SerialName("type") val type: Int
    )

    @Serializable
    data class Adver(
        @SerialName("adver_id") val adverId: Int,
        @SerialName("adver_type") val adverType: Int
    )

    @Serializable
    data class Cover(
        @SerialName("desc") val desc: String,
        @SerialName("gif_tag_show") val gifTagShow: Boolean,
        @SerialName("image_height") val imageHeight: Int,
        @SerialName("image_width") val imageWidth: Int,
        @SerialName("jump_url") val jumpUrl: String,
        @SerialName("loop") val loop: Int,
        @SerialName("title") val title: String,
        @SerialName("url") val url: String
    )

    @Serializable
    data class FeedbackPanel(
        @SerialName("close_rec_tips") val closeRecTips: String,
//    @SerialName("feedback_panel_detail") val feedbackPanelDetail: List<Any>,
        @SerialName("open_rec_tips") val openRecTips: String,
        @SerialName("panel_type_text") val panelTypeText: String,
        @SerialName("toast") val toast: String
    )
}



