package top.sacz.bili.biz.user.entity.mine

import top.sacz.bili.biz.user.entity.Label
import top.sacz.bili.biz.user.entity.Vip

//https://app.bilibili.com/x/v2/account/mine?access_key=
data class Mine(
    val achievement: Achievement,
    val audio_type: Int,
    val avatar: Avatar,
    val bcoin: Int,
    val bubbles: Any,
    val coin: Int,
    val `dynamic`: Int,
    val enable_bili_link: Boolean,
    val face: String,
    val face_nft_new: Int,
    val first_live_time: Int,
    val follower: Int,
    val following: Int,
    val game_tip: List<GameTip>,
    val in_reg_audit: Int,
    val level: Int,
    val mall_home: MallHome,
    val mid: Int,
    val modular_vip_section: ModularVipSection,
    val name: String,
    val name_render: Any,
    val new_followers: Int,
    val new_followers_rtime: Int,
    val official_verify: OfficialVerify,
    val rank: Int,
    val rework_v1: ReworkV1,
    val sections_v2: List<SectionsV2>,
    val senior_gate: SeniorGate,
    val sex: Int,
    val show_creative: Int,
    val show_face_guide: Boolean,
    val show_name_guide: Boolean,
    val show_nft_face_guide: Boolean,
    val show_videoup: Int,
    val silence: Int,
    val use_modular_vip_section: Boolean,
    val vip: Vip,
    val vip_type: Int
)

class Achievement

data class Avatar(
    val container_size: ContainerSize,
    val fallback_layers: FallbackLayers,
    val layers: List<LayerX>,
    val mid: String
)

data class GameTip(
    val content: String,
    val icon: String,
    val id: Int,
    val is_directed: Int,
    val url: String
)

data class MallHome(
    val icon: String,
    val title: String,
    val uri: String
)

data class ModularVipSection(
    val background: Background,
    val button: Button,
    val button_icon: Any,
    val extra: Extra,
    val logo: Logo,
    val subtitle: Subtitle,
    val title: Title
)

data class OfficialVerify(
    val desc: String,
    val type: Int
)

data class ReworkV1(
    val new_mine: Boolean,
    val original_num: Int,
    val user_original_state: Int,
    val worst_creative: WorstCreative
)

data class SectionsV2(
    val button: ButtonX,
    val items: List<Item>,
    val style: Int,
    val title: String,
    val type: Int,
    val up_title: String
)

data class SeniorGate(
    val birthday_conf: Any,
    val bubble: String,
    val member_text: String
)

data class Vip(
    val avatar_subscript: Int,
    val avatar_subscript_url: String,
    val due_date: Long,
    val label: Label,
    val nickname_color: String,
    val role: Int,
    val status: Int,
    val themeType: Int,
    val theme_type: Int,
    val type: Int,
    val vip_pay_type: Int
)

data class ContainerSize(
    val height: Double,
    val width: Double
)

data class FallbackLayers(
    val is_critical_group: Boolean,
    val layers: List<Layer>
)

data class LayerX(
    val is_critical_group: Boolean,
    val layers: List<LayerXX>
)

data class Layer(
    val general_spec: GeneralSpec,
    val layer_config: LayerConfig,
    val resource: Resource,
    val visible: Boolean
)

data class GeneralSpec(
    val pos_spec: PosSpec,
    val render_spec: RenderSpec,
    val size_spec: SizeSpec
)

data class LayerConfig(
    val is_critical: Boolean,
    val layer_mask: LayerMask,
    val tags: Tags
)

data class Resource(
    val res_image: ResImage,
    val res_native_draw: ResNativeDraw,
    val res_type: Int
)

data class PosSpec(
    val axis_x: Double,
    val axis_y: Double,
    val coordinate_pos: Int
)

data class RenderSpec(
    val opacity: Int
)

data class SizeSpec(
    val height: Double,
    val width: Double
)

data class LayerMask(
    val general_spec: GeneralSpecX,
    val mask_src: MaskSrc
)

data class Tags(
    val AVATAR_LAYER: AVATARLAYER
)

data class GeneralSpecX(
    val pos_spec: PosSpec,
    val render_spec: RenderSpec,
    val size_spec: SizeSpecX
)

data class MaskSrc(
    val draw: Draw,
    val src_type: Int
)

data class SizeSpecX(
    val height: Int,
    val width: Int
)

data class Draw(
    val color_config: ColorConfig,
    val draw_type: Int,
    val fill_mode: Int
)

data class ColorConfig(
    val day: Day
)

data class Day(
    val argb: String
)

class AVATARLAYER

data class ResImage(
    val image_src: ImageSrc
)

data class ResNativeDraw(
    val draw_src: DrawSrc
)

data class ImageSrc(
    val placeholder: Int,
    val remote: Remote,
    val src_type: Int
)

data class Remote(
    val bfs_style: String,
    val url: String
)

data class DrawSrc(
    val draw: DrawX,
    val src_type: Int
)

data class DrawX(
    val color_config: ColorConfigX,
    val draw_type: Int,
    val fill_mode: Int
)

data class ColorConfigX(
    val day: Day,
    val is_dark_mode_aware: Boolean,
    val night: Night
)

data class Night(
    val argb: String
)

data class LayerXX(
    val general_spec: GeneralSpec,
    val layer_config: LayerConfigX,
    val resource: Resource,
    val visible: Boolean
)

data class LayerConfigX(
    val is_critical: Boolean,
    val layer_mask: LayerMask,
    val tags: TagsX
)

data class TagsX(
    val AVATAR_LAYER: AVATARLAYER,
    val DARK_MODE_OVERRIDE_ASSOCIATED: DARKMODEOVERRIDEASSOCIATED,
    val DARK_MODE_OVERRIDE_CFG: DARKMODEOVERRIDECFG
)

class DARKMODEOVERRIDEASSOCIATED

class DARKMODEOVERRIDECFG

data class Background(
    val color: Color,
    val frequency_control: Boolean,
    val image: String
)

data class Button(
    val background_style: BackgroundStyle,
    val style: Int,
    val text: String,
    val text_style: TextStyle,
    val url: String
)

data class Extra(
    val marketing_subtype: Int,
    val module_type: Int,
    val track_params: TrackParams
)

data class Logo(
    val light_icon: String,
    val night_icon: String
)

data class Subtitle(
    val text: String,
    val text_style: TextStyleX
)

data class Title(
    val text: String,
    val text_style: TextStyle,
    val url: String
)

data class Color(
    val dark: Dark,
    val light: Light
)

data class Dark(
    val alpha: Double,
    val value: String
)

data class Light(
    val alpha: Double,
    val value: String
)

data class BackgroundStyle(
    val dark: DarkX,
    val light: LightX
)

data class TextStyle(
    val dark: DarkX,
    val light: LightX
)

data class DarkX(
    val alpha: Int,
    val value: String
)

data class LightX(
    val alpha: Int,
    val value: String
)

data class TrackParams(
    val act_id: String,
    val ads_src: String,
    val buvid: String,
    val exp_group_tag: String,
    val exp_tag: String,
    val material_type: String,
    val mid: String,
    val position_id: String,
    val request_id: String,
    val source_from: String,
    val tips_id: String,
    val tips_repeat_key: String,
    val unit_id: String,
    val vip_status: String,
    val vip_type: String
)

data class TextStyleX(
    val dark: Dark,
    val light: Light
)

data class WorstCreative(
    val button_icon: String,
    val button_link: String,
    val button_text: String,
    val title: String
)

data class ButtonX(
    val icon: String,
    val style: Int,
    val text: String,
    val url: String
)

data class Item(
    val common_op_item: CommonOpItem,
    val display: Int,
    val global_red_dot: Int,
    val icon: String,
    val id: Int,
    val is_up_anchor: Boolean,
    val need_login: Int,
    val red_dot: Int,
    val red_dot_for_new: Boolean,
    val title: String,
    val uri: String
)

class CommonOpItem

data class Label(
    val bg_color: String,
    val bg_style: Int,
    val border_color: String,
    val image: String,
    val label_theme: String,
    val path: String,
    val text: String,
    val text_color: String
)