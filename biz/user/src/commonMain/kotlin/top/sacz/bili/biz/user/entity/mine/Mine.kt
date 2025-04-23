package top.sacz.bili.biz.user.entity.mine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 注释起来的都是不需要的 取消注释会直接因为请求体和类型不一致报错
 * 报错包含null 或者 {} 空的请求体
 * 没有必要为了不使用的东西去专门适配一遍
 */
@Serializable
data class Mine(
//    @SerialName("achievement") val achievement: Achievement, // 用户成就
//    @SerialName("audio_type") val audioType: Int, // 音频类型
//    @SerialName("avatar") val avatar: Avatar, // 用户头像
    @SerialName("bcoin") val bcoin: Int, // B币数量
//    @SerialName("bubbles") val bubbles: Unknown?, // 气泡
    @SerialName("coin") val coin: Int, // 硬币数量
    @SerialName("dynamic") val dynamic: Int, // 动态数量
    @SerialName("enable_bili_link") val enableBiliLink: Boolean, // 是否启用B站链接
    @SerialName("face") val face: String, // 用户头像URL
    @SerialName("face_nft_new") val faceNftNew: Int, // NFT头像状态
    @SerialName("first_live_time") val firstLiveTime: Int, // 首次直播时间
    @SerialName("follower") val follower: Int, // 粉丝数量
    @SerialName("following") val following: Int, // 关注数量
//    @SerialName("game_tip") val gameTip: List<GameTip>, // 游戏提示
    @SerialName("in_reg_audit") val inRegAudit: Int, // 注册审核状态
    @SerialName("level") val level: Int, // 用户等级
//    @SerialName("mall_home") val mallHome: MallHome, // 商城首页
    @SerialName("mid") val mid: Int, // 用户ID
//    @SerialName("modular_vip_section") val modularVipSection: ModularVipSection, // VIP模块
    @SerialName("name") val name: String, // 用户昵称
//    @SerialName("name_render") val nameRender: Unknown?, // 昵称渲染
    @SerialName("new_followers") val newFollowers: Int, // 新粉丝数量
    @SerialName("new_followers_rtime") val newFollowersRtime: Int, // 新粉丝时间
    @SerialName("official_verify") val officialVerify: OfficialVerify, // 官方认证
    @SerialName("rank") val rank: Int, // 用户排名
//    @SerialName("rework_v1") val reworkV1: ReworkV1, // 重做V1
//    @SerialName("sections_v2") val sectionsV2: List<SectionsV2>, // V2模块
//    @SerialName("senior_gate") val seniorGate: SeniorGate, // 高级门
    @SerialName("sex") val sex: Int, // 性别
    @SerialName("show_creative") val showCreative: Int, // 显示创意
    @SerialName("show_face_guide") val showFaceGuide: Boolean, // 显示头像引导
    @SerialName("show_name_guide") val showNameGuide: Boolean, // 显示昵称引导
    @SerialName("show_nft_face_guide") val showNftFaceGuide: Boolean, // 显示NFT头像引导
    @SerialName("show_videoup") val showVideoup: Int, // 显示视频上传
    @SerialName("silence") val silence: Int, // 禁言状态
//    @SerialName("use_modular_vip_section") val useModularVipSection: Boolean, // 使用VIP模块
    @SerialName("vip") val vip: Vip, // VIP信息
    @SerialName("vip_type") val vipType: Int // VIP类型
)

@Serializable
class Achievement

//有此字段说明在响应体中没有值,只是我的号没有,别的号可能会有
@Serializable
class Unknown

@Serializable
data class Avatar(
    @SerialName("container_size") val containerSize: ContainerSize,
    @SerialName("fallback_layers") val fallbackLayers: FallbackLayers,
    @SerialName("layers") val layers: List<LayerX>,
    @SerialName("mid") val mid: String
)

@Serializable
data class GameTip(
    @SerialName("content") val content: String,
    @SerialName("icon") val icon: String,
    @SerialName("id") val id: Int,
    @SerialName("is_directed") val isDirected: Int,
    @SerialName("url") val url: String
)

@Serializable
data class MallHome(
    @SerialName("icon") val icon: String,
    @SerialName("title") val title: String,
    @SerialName("uri") val uri: String
)

@Serializable
data class ModularVipSection(
    @SerialName("background") val background: Background,
    @SerialName("button") val button: Button,
    @SerialName("button_icon") val buttonIcon: Unknown?,
    @SerialName("extra") val extra: Extra,
    @SerialName("logo") val logo: Logo,
    @SerialName("subtitle") val subtitle: Subtitle,
    @SerialName("title") val title: Title
)

@Serializable
data class OfficialVerify(
    @SerialName("desc") val desc: String,
    @SerialName("type") val type: Int
)

@Serializable
data class ReworkV1(
    @SerialName("new_mine") val newMine: Boolean,
    @SerialName("original_num") val originalNum: Int,
    @SerialName("user_original_state") val userOriginalState: Int,
    @SerialName("worst_creative") val worstCreative: WorstCreative
)

@Serializable
data class SectionsV2(
    @SerialName("button") val button: ButtonX,
    @SerialName("items") val items: MutableList<Item>,
    @SerialName("style") val style: Int,
    @SerialName("title") val title: String,
    @SerialName("type") val type: Int,
    @SerialName("up_title") val upTitle: String
)

@Serializable
data class SeniorGate(
    @SerialName("birthday_conf") val birthdayConf: Unknown?,
    @SerialName("bubble") val bubble: String,
    @SerialName("member_text") val memberText: String
)

@Serializable
data class Vip(
    @SerialName("avatar_subscript") val avatarSubscript: Int,
    @SerialName("avatar_subscript_url") val avatarSubscriptUrl: String,
    @SerialName("due_date") val dueDate: Long,
    @SerialName("label") val label: Label,
    @SerialName("nickname_color") val nicknameColor: String,
    @SerialName("role") val role: Int,
    @SerialName("status") val status: Int,
    @SerialName("themeType") val themeType: Int,
    @SerialName("theme_type") val theme_Type: Int,
    @SerialName("type") val type: Int,
    @SerialName("vip_pay_type") val vipPayType: Int
)

@Serializable
data class ContainerSize(
    @SerialName("height") val height: Double,
    @SerialName("width") val width: Double
)

@Serializable
data class FallbackLayers(
    @SerialName("is_critical_group") val isCriticalGroup: Boolean,
    @SerialName("layers") val layers: List<Layer>
)

@Serializable
data class LayerX(
    @SerialName("is_critical_group") val isCriticalGroup: Boolean,
    @SerialName("layers") val layers: List<LayerXX>
)

@Serializable
data class Layer(
    @SerialName("general_spec") val generalSpec: GeneralSpec,
    @SerialName("layer_config") val layerConfig: LayerConfig,
    @SerialName("resource") val resource: Resource,
    @SerialName("visible") val visible: Boolean
)

@Serializable
data class GeneralSpec(
    @SerialName("pos_spec") val posSpec: PosSpec,
    @SerialName("render_spec") val renderSpec: RenderSpec,
    @SerialName("size_spec") val sizeSpec: SizeSpec
)

@Serializable
data class LayerConfig(
    @SerialName("is_critical") val isCritical: Boolean,
    @SerialName("layer_mask") val layerMask: LayerMask,
    @SerialName("tags") val tags: Tags
)

@Serializable
data class Resource(
    @SerialName("res_image") val resImage: ResImage,
    @SerialName("res_native_draw") val resNativeDraw: ResNativeDraw,
    @SerialName("res_type") val resType: Int
)

@Serializable
data class PosSpec(
    @SerialName("axis_x") val axisX: Double,
    @SerialName("axis_y") val axisY: Double,
    @SerialName("coordinate_pos") val coordinatePos: Int
)

@Serializable
data class RenderSpec(
    @SerialName("opacity") val opacity: Int
)

@Serializable
data class SizeSpec(
    @SerialName("height") val height: Double,
    @SerialName("width") val width: Double
)

@Serializable
data class LayerMask(
    @SerialName("general_spec") val generalSpec: GeneralSpecX,
    @SerialName("mask_src") val maskSrc: MaskSrc
)

@Serializable
data class Tags(
    @SerialName("AVATAR_LAYER") val avatarLayer: AVATARLAYER
)

@Serializable
data class GeneralSpecX(
    @SerialName("pos_spec") val posSpec: PosSpec,
    @SerialName("render_spec") val renderSpec: RenderSpec,
    @SerialName("size_spec") val sizeSpec: SizeSpecX
)

@Serializable
data class MaskSrc(
    @SerialName("draw") val draw: Draw,
    @SerialName("src_type") val srcType: Int
)

@Serializable
data class SizeSpecX(
    @SerialName("height") val height: Int,
    @SerialName("width") val width: Int
)

@Serializable
data class Draw(
    @SerialName("color_config") val colorConfig: ColorConfig,
    @SerialName("draw_type") val drawType: Int,
    @SerialName("fill_mode") val fillMode: Int
)

@Serializable
data class ColorConfig(
    @SerialName("day") val day: Day
)

@Serializable
data class Day(
    @SerialName("argb") val argb: String
)

@Serializable
class AVATARLAYER

@Serializable
data class ResImage(
    @SerialName("image_src") val imageSrc: ImageSrc
)

@Serializable
data class ResNativeDraw(
    @SerialName("draw_src") val drawSrc: DrawSrc
)

@Serializable
data class ImageSrc(
    @SerialName("placeholder") val placeholder: Int,
    @SerialName("remote") val remote: Remote,
    @SerialName("src_type") val srcType: Int
)

@Serializable
data class Remote(
    @SerialName("bfs_style") val bfsStyle: String,
    @SerialName("url") val url: String
)

@Serializable
data class DrawSrc(
    @SerialName("draw") val draw: DrawX,
    @SerialName("src_type") val srcType: Int
)

@Serializable
data class DrawX(
    @SerialName("color_config") val colorConfig: ColorConfigX,
    @SerialName("draw_type") val drawType: Int,
    @SerialName("fill_mode") val fillMode: Int
)

@Serializable
data class ColorConfigX(
    @SerialName("day") val day: Day,
    @SerialName("is_dark_mode_aware") val isDarkModeAware: Boolean,
    @SerialName("night") val night: Night
)

@Serializable
data class Night(
    @SerialName("argb") val argb: String
)

@Serializable
data class LayerXX(
    @SerialName("general_spec") val generalSpec: GeneralSpec,
    @SerialName("layer_config") val layerConfig: LayerConfigX,
    @SerialName("resource") val resource: Resource,
    @SerialName("visible") val visible: Boolean
)

@Serializable
data class LayerConfigX(
    @SerialName("is_critical") val isCritical: Boolean,
    @SerialName("layer_mask") val layerMask: LayerMask,
    @SerialName("tags") val tags: TagsX
)

@Serializable
data class TagsX(
    @SerialName("AVATAR_LAYER") val avatarLayer: AVATARLAYER,
    @SerialName("DARK_MODE_OVERRIDE_ASSOCIATED") val darkModeOverrideAssociated: DARKMODEOVERRIDEASSOCIATED,
    @SerialName("DARK_MODE_OVERRIDE_CFG") val darkModeOverrideCfg: DARKMODEOVERRIDECFG
)

@Serializable
class DARKMODEOVERRIDEASSOCIATED

@Serializable
class DARKMODEOVERRIDECFG

@Serializable
data class Background(
    @SerialName("color") val color: Color,
    @SerialName("frequency_control") val frequencyControl: Boolean,
    @SerialName("image") val image: String
)

@Serializable
data class Button(
    @SerialName("background_style") val backgroundStyle: BackgroundStyle,
    @SerialName("style") val style: Int,
    @SerialName("text") val text: String,
    @SerialName("text_style") val textStyle: TextStyle,
    @SerialName("url") val url: String
)

@Serializable
data class Extra(
    @SerialName("marketing_subtype") val marketingSubtype: Int,
    @SerialName("module_type") val moduleType: Int,
    @SerialName("track_params") val trackParams: TrackParams
)

@Serializable
data class Logo(
    @SerialName("light_icon") val lightIcon: String,
    @SerialName("night_icon") val nightIcon: String
)

@Serializable
data class Subtitle(
    @SerialName("text") val text: String,
    @SerialName("text_style") val textStyle: TextStyleX
)

@Serializable
data class Title(
    @SerialName("text") val text: String,
    @SerialName("text_style") val textStyle: TextStyle,
    @SerialName("url") val url: String
)

@Serializable
data class Color(
    @SerialName("dark") val dark: Dark,
    @SerialName("light") val light: Light
)

@Serializable
data class Dark(
    @SerialName("alpha") val alpha: Double,
    @SerialName("value") val value: String
)

@Serializable
data class Light(
    @SerialName("alpha") val alpha: Double,
    @SerialName("value") val value: String
)

@Serializable
data class BackgroundStyle(
    @SerialName("dark") val dark: DarkX,
    @SerialName("light") val light: LightX
)

@Serializable
data class TextStyle(
    @SerialName("dark") val dark: DarkX,
    @SerialName("light") val light: LightX
)

@Serializable
data class DarkX(
    @SerialName("alpha") val alpha: Double,
    @SerialName("value") val value: String
)

@Serializable
data class LightX(
    @SerialName("alpha") val alpha: Double,
    @SerialName("value") val value: String
)

@Serializable
data class TrackParams(
    @SerialName("act_id") val actId: String,
    @SerialName("ads_src") val adsSrc: String?,
    @SerialName("buvid") val buvid: String,
    @SerialName("exp_group_tag") val expGroupTag: String,
    @SerialName("exp_tag") val expTag: String,
    @SerialName("material_type") val materialType: String,
    @SerialName("mid") val mid: String,
    @SerialName("position_id") val positionId: String,
    @SerialName("request_id") val requestId: String,
    @SerialName("source_from") val sourceFrom: String,
    @SerialName("tips_id") val tipsId: String,
    @SerialName("tips_repeat_key") val tipsRepeatKey: String,
    @SerialName("unit_id") val unitId: String,
    @SerialName("vip_status") val vipStatus: String,
    @SerialName("vip_type") val vipType: String
)

@Serializable
data class TextStyleX(
    @SerialName("dark") val dark: Dark,
    @SerialName("light") val light: Light
)

@Serializable
data class WorstCreative(
    @SerialName("button_icon") val buttonIcon: String,
    @SerialName("button_link") val buttonLink: String,
    @SerialName("button_text") val buttonText: String,
    @SerialName("title") val title: String
)

@Serializable
data class ButtonX(
    @SerialName("icon") val icon: String,
    @SerialName("style") val style: Int,
    @SerialName("text") val text: String,
    @SerialName("url") val url: String
)

@Serializable
data class Item(
    @SerialName("common_op_item") val commonOpItem: CommonOpItem,
    @SerialName("display") val display: Int,
    @SerialName("global_red_dot") val globalRedDot: Int,
    @SerialName("icon") val icon: String,
    @SerialName("id") val id: Int,
    @SerialName("is_up_anchor") val isUpAnchor: Boolean,
    @SerialName("need_login") val needLogin: Int,
    @SerialName("red_dot") val redDot: Int,
    @SerialName("red_dot_for_new") val redDotForNew: Boolean,
    @SerialName("title") val title: String,
    @SerialName("uri") val uri: String
)

@Serializable
class CommonOpItem

@Serializable
data class Label(
    @SerialName("bg_color") val bgColor: String,
    @SerialName("bg_style") val bgStyle: Int,
    @SerialName("border_color") val borderColor: String,
    @SerialName("image") val image: String,
    @SerialName("label_theme") val labelTheme: String,
    @SerialName("path") val path: String,
    @SerialName("text") val text: String,
    @SerialName("text_color") val textColor: String
)