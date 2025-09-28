package top.suzhelan.bili.comment.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 超复杂评论区条目对象
 */
@Serializable
data class Comment(
    @SerialName("action")
    val action: Int, // 0
    @SerialName("assist")
    val assist: Int, // 0
    @SerialName("attr")
    val attr: Int, // 0
    @SerialName("content")
    val content: Content,
    @SerialName("count")
    val count: Int, // 0
    @SerialName("ctime")
    val ctime: Int, // 1759050606
    @SerialName("dialog")
    val dialog: Int, // 0
    @SerialName("dialog_str")
    val dialogStr: String, // 0
    @SerialName("dynamic_id_str")
    val dynamicIdStr: String, // 0
    @SerialName("fansgrade")
    val fansgrade: Int, // 0
    @SerialName("folder")
    val folder: Folder,
    @SerialName("invisible")
    val invisible: Boolean, // false
    @SerialName("like")
    val like: Int, // 0
    @SerialName("member")
    val member: Member,
    @SerialName("mid")
    val mid: Int, // 378491392
    @SerialName("mid_str")
    val midStr: String, // 378491392
    @SerialName("note_cvid_str")
    val noteCvidStr: String, // 0
    @SerialName("oid")
    val oid: Long, // 115280630192742
    @SerialName("oid_str")
    val oidStr: String, // 115280630192742
    @SerialName("parent")
    val parent: Int, // 0
    @SerialName("parent_str")
    val parentStr: String, // 0
    @SerialName("rcount")
    val rcount: Int, // 0
    @SerialName("replies")
    val replies: List<Comment>?, // 评论回复条目预览	仅嵌套一层 否则为 null
    @SerialName("reply_control")
    val replyControl: ReplyControl,
    @SerialName("root")
    val root: Int, // 0
    @SerialName("root_str")
    val rootStr: String, // 0
    @SerialName("rpid")
    val rpid: Long, // 275929471521
    @SerialName("rpid_str")
    val rpidStr: String, // 275929471521
    @SerialName("state")
    val state: Int, // 0
    @SerialName("track_info")
    val trackInfo: String,
    @SerialName("type")
    val type: Int, // 1
    @SerialName("up_action")
    val upAction: UpAction
) {
    @Serializable
    data class Content(
        @SerialName("emote")
        val emote: Map<String, Emote>,
        @SerialName("jump_url")
        val jumpUrl: JumpUrl,
        @SerialName("max_line")
        val maxLine: Int, // 6
        @SerialName("members")
        val members: List<Member>,
        @SerialName("message")
        val message: String, // 宝宝好可爱[喜欢],
        val pictures: List<Picture>
    ) {
        @Serializable
        data class Picture(
            @SerialName("img_src")
            val imgSrc: String,
            @SerialName("img_size")
            val imgSize: List<Int>,
            @SerialName("img_width")
            val imgWidth: Int, // 0
            @SerialName("img_height")
            val imgHeight: Int, // 0
        )

        @Serializable
        data class Emote(
            @SerialName("attr")
            val attr: Int, // 0
            @SerialName("id")
            val id: Int, // 3
            @SerialName("jump_title")
            val jumpTitle: String, // 喜欢
            @SerialName("meta")
            val meta: Meta,
            @SerialName("mtime")
            val mtime: Int, // 1668688325
            @SerialName("package_id")
            val packageId: Int, // 1
            @SerialName("state")
            val state: Int, // 0
            @SerialName("text")
            val text: String, // [喜欢]
            @SerialName("type")
            val type: Int, // 1
            @SerialName("url")
            val url: String // https://i0.hdslb.com/bfs/emote/8a10a4d73a89f665feff3d46ca56e83dc68f9eb8.png
        ) {
            @Serializable
            data class Meta(
                @SerialName("size")
                val size: Int, // 1
                @SerialName("suggest")
                val suggest: List<String>
            )
        }


        @Serializable
        class JumpUrl
    }

    @Serializable
    data class Folder(
        @SerialName("has_folded")
        val hasFolded: Boolean, // false
        @SerialName("is_folded")
        val isFolded: Boolean, // false
        @SerialName("rule")
        val rule: String
    )

    @Serializable
    data class Member(
        @SerialName("avatar")
        val avatar: String, // https://i1.hdslb.com/bfs/face/b8bf52bb209be7d6a531ea318af0bf860bc443c6.jpg
        @SerialName("avatar_item")
        val avatarItem: AvatarItem,
        @SerialName("contract_desc")
        val contractDesc: String,
        @SerialName("face_nft_new")
        val faceNftNew: Int, // 0
        @SerialName("is_contractor")
        val isContractor: Boolean, // false
        @SerialName("is_senior_member")
        val isSeniorMember: Int, // 1
        @SerialName("level_info")
        val levelInfo: LevelInfo,
        @SerialName("mid")
        val mid: String, // 378491392
        @SerialName("nameplate")
        val nameplate: Nameplate,
        @SerialName("official_verify")
        val officialVerify: OfficialVerify,
        @SerialName("pendant")
        val pendant: Pendant,
        @SerialName("rank")
        val rank: String, // 10000
        @SerialName("senior")
        val senior: Senior,
        @SerialName("sex")
        val sex: String, // 保密
        @SerialName("sign")
        val sign: String,
        @SerialName("uname")
        val uname: String, // ゚゚生盐诺亚
        @SerialName("vip")
        val vip: Vip
    ) {
        @Serializable
        data class AvatarItem(
            @SerialName("container_size")
            val containerSize: ContainerSize,
            @SerialName("fallback_layers")
            val fallbackLayers: FallbackLayers,
            @SerialName("mid")
            val mid: String // 378491392
        ) {
            @Serializable
            data class ContainerSize(
                @SerialName("height")
                val height: Double, // 1.8
                @SerialName("width")
                val width: Double // 1.8
            )

            @Serializable
            data class FallbackLayers(
                @SerialName("is_critical_group")
                val isCriticalGroup: Boolean, // true
                @SerialName("layers")
                val layers: List<Layer>
            ) {
                @Serializable
                data class Layer(
                    @SerialName("general_spec")
                    val generalSpec: GeneralSpec,
                    @SerialName("layer_config")
                    val layerConfig: LayerConfig,
                    @SerialName("resource")
                    val resource: Resource,
                    @SerialName("visible")
                    val visible: Boolean // true
                ) {
                    @Serializable
                    data class GeneralSpec(
                        @SerialName("pos_spec")
                        val posSpec: PosSpec,
                        @SerialName("render_spec")
                        val renderSpec: RenderSpec,
                        @SerialName("size_spec")
                        val sizeSpec: SizeSpec
                    ) {
                        @Serializable
                        data class PosSpec(
                            @SerialName("axis_x")
                            val axisX: Double, // 0.9
                            @SerialName("axis_y")
                            val axisY: Double, // 0.9
                            @SerialName("coordinate_pos")
                            val coordinatePos: Int // 2
                        )

                        @Serializable
                        data class RenderSpec(
                            @SerialName("opacity")
                            val opacity: Int // 1
                        )

                        @Serializable
                        data class SizeSpec(
                            @SerialName("height")
                            val height: Double, // 0.4
                            @SerialName("width")
                            val width: Double // 0.4
                        )
                    }

                    @Serializable
                    data class LayerConfig(
                        @SerialName("is_critical")
                        val isCritical: Boolean?, // true
                        @SerialName("layer_mask")
                        val layerMask: LayerMask?,
                        @SerialName("tags")
                        val tags: Tags
                    ) {
                        @Serializable
                        data class LayerMask(
                            @SerialName("general_spec")
                            val generalSpec: GeneralSpec,
                            @SerialName("mask_src")
                            val maskSrc: MaskSrc
                        ) {
                            @Serializable
                            data class GeneralSpec(
                                @SerialName("pos_spec")
                                val posSpec: PosSpec,
                                @SerialName("render_spec")
                                val renderSpec: RenderSpec,
                                @SerialName("size_spec")
                                val sizeSpec: SizeSpec
                            ) {
                                @Serializable
                                data class PosSpec(
                                    @SerialName("axis_x")
                                    val axisX: Double, // 0.9
                                    @SerialName("axis_y")
                                    val axisY: Double, // 0.9
                                    @SerialName("coordinate_pos")
                                    val coordinatePos: Int // 2
                                )

                                @Serializable
                                data class RenderSpec(
                                    @SerialName("opacity")
                                    val opacity: Int // 1
                                )

                                @Serializable
                                data class SizeSpec(
                                    @SerialName("height")
                                    val height: Int, // 1
                                    @SerialName("width")
                                    val width: Int // 1
                                )
                            }

                            @Serializable
                            data class MaskSrc(
                                @SerialName("draw")
                                val draw: Draw,
                                @SerialName("src_type")
                                val srcType: Int // 3
                            ) {
                                @Serializable
                                data class Draw(
                                    @SerialName("color_config")
                                    val colorConfig: ColorConfig,
                                    @SerialName("draw_type")
                                    val drawType: Int, // 1
                                    @SerialName("fill_mode")
                                    val fillMode: Int // 1
                                ) {
                                    @Serializable
                                    data class ColorConfig(
                                        @SerialName("day")
                                        val day: Day
                                    ) {
                                        @Serializable
                                        data class Day(
                                            @SerialName("argb")
                                            val argb: String // #FF000000
                                        )
                                    }
                                }
                            }
                        }

                        @Serializable
                        data class Tags(
                            @SerialName("AVATAR_LAYER")
                            val aVATARLAYER: AVATARLAYER?,
                            @SerialName("ICON_LAYER")
                            val iCONLAYER: ICONLAYER?
                        ) {
                            @Serializable
                            class AVATARLAYER

                            @Serializable
                            class ICONLAYER
                        }
                    }

                    @Serializable
                    data class Resource(
                        @SerialName("res_image")
                        val resImage: ResImage?,
                        @SerialName("res_native_draw")
                        val resNativeDraw: ResNativeDraw?,
                        @SerialName("res_type")
                        val resType: Int // 3
                    ) {
                        @Serializable
                        data class ResImage(
                            @SerialName("image_src")
                            val imageSrc: ImageSrc
                        ) {
                            @Serializable
                            data class ImageSrc(
                                @SerialName("placeholder")
                                val placeholder: Int, // 6
                                @SerialName("remote")
                                val remote: Remote,
                                @SerialName("src_type")
                                val srcType: Int // 1
                            ) {
                                @Serializable
                                data class Remote(
                                    @SerialName("bfs_style")
                                    val bfsStyle: String, // widget-layer-avatar
                                    @SerialName("url")
                                    val url: String // https://i1.hdslb.com/bfs/face/b8bf52bb209be7d6a531ea318af0bf860bc443c6.jpg
                                )
                            }
                        }

                        @Serializable
                        data class ResNativeDraw(
                            @SerialName("draw_src")
                            val drawSrc: DrawSrc
                        ) {
                            @Serializable
                            data class DrawSrc(
                                @SerialName("draw")
                                val draw: Draw,
                                @SerialName("src_type")
                                val srcType: Int // 3
                            ) {
                                @Serializable
                                data class Draw(
                                    @SerialName("color_config")
                                    val colorConfig: ColorConfig,
                                    @SerialName("draw_type")
                                    val drawType: Int, // 1
                                    @SerialName("fill_mode")
                                    val fillMode: Int // 1
                                ) {
                                    @Serializable
                                    data class ColorConfig(
                                        @SerialName("day")
                                        val day: Day,
                                        @SerialName("is_dark_mode_aware")
                                        val isDarkModeAware: Boolean, // true
                                        @SerialName("night")
                                        val night: Night
                                    ) {
                                        @Serializable
                                        data class Day(
                                            @SerialName("argb")
                                            val argb: String // #FFFFFFFF
                                        )

                                        @Serializable
                                        data class Night(
                                            @SerialName("argb")
                                            val argb: String // #FF17181A
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Serializable
        data class LevelInfo(
            @SerialName("current_exp")
            val currentExp: Int, // 0
            @SerialName("current_level")
            val currentLevel: Int, // 6
            @SerialName("current_min")
            val currentMin: Int, // 0
            @SerialName("next_exp")
            val nextExp: Int // 0
        )

        @Serializable
        data class Nameplate(
            @SerialName("condition")
            val condition: String, // 所有自制视频总播放数>=100万，数据次日更新
            @SerialName("image")
            val image: String, // https://i0.hdslb.com/bfs/face/27a952195555e64508310e366b3e38bd4cd143fc.png
            @SerialName("image_small")
            val imageSmall: String, // https://i1.hdslb.com/bfs/face/0497be49e08357bf05bca56e33a0637a273a7610.png
            @SerialName("level")
            val level: String, // 稀有勋章
            @SerialName("name")
            val name: String, // 知名偶像
            @SerialName("nid")
            val nid: Int // 8
        )

        @Serializable
        data class OfficialVerify(
            @SerialName("desc")
            val desc: String,
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
        data class Senior(
            @SerialName("status")
            val status: Int // 2
        )

        @Serializable
        data class Vip(
            @SerialName("accessStatus")
            val accessStatus: Int, // 0
            @SerialName("avatar_subscript")
            val avatarSubscript: Int, // 1
            @SerialName("dueRemark")
            val dueRemark: String,
            @SerialName("label")
            val label: Label,
            @SerialName("nickname_color")
            val nicknameColor: String, // #FB7299
            @SerialName("themeType")
            val themeType: Int, // 0
            @SerialName("vipDueDate")
            val vipDueDate: Long, // 1791043200000
            @SerialName("vipStatus")
            val vipStatus: Int, // 1
            @SerialName("vipStatusWarn")
            val vipStatusWarn: String,
            @SerialName("vipType")
            val vipType: Int // 2
        ) {
            @Serializable
            data class Label(
                @SerialName("bg_color")
                val bgColor: String, // #FB7299
                @SerialName("bg_style")
                val bgStyle: Int, // 1
                @SerialName("border_color")
                val borderColor: String,
                @SerialName("img_label_uri_hans")
                val imgLabelUriHans: String,
                @SerialName("img_label_uri_hans_static")
                val imgLabelUriHansStatic: String, // https://i0.hdslb.com/bfs/bangumi/kt/e74c74edc802bc4badba15f03e69d7cff75f2679.png
                @SerialName("img_label_uri_hant")
                val imgLabelUriHant: String,
                @SerialName("img_label_uri_hant_static")
                val imgLabelUriHantStatic: String, // https://i0.hdslb.com/bfs/activity-plat/static/20220614/e369244d0b14644f5e1a06431e22a4d5/VEW8fCC0hg.png
                @SerialName("label_goto")
                val labelGoto: LabelGoto,
                @SerialName("label_id")
                val labelId: Int, // 47
                @SerialName("label_theme")
                val labelTheme: String, // annual_vip
                @SerialName("path")
                val path: String, // http://i0.hdslb.com/bfs/vip/label_annual.png
                @SerialName("text")
                val text: String, // 年度大会员
                @SerialName("text_color")
                val textColor: String, // #FFFFFF
                @SerialName("use_img_label")
                val useImgLabel: Boolean // true
            ) {
                @Serializable
                data class LabelGoto(
                    @SerialName("mobile")
                    val mobile: String, // https://big.bilibili.com/mobile/index?appId=125&appSubId=minebutton&banner_id=34281&popup_id=34279
                    @SerialName("pc_web")
                    val pcWeb: String // https://account.bilibili.com/big?from_spmid=vipicon
                )
            }
        }
    }

    @Serializable
    data class ReplyControl(
        @SerialName("location")
        val location: String, // IP属地：广东
        @SerialName("max_line")
        val maxLine: Int, // 6
        @SerialName("time_desc")
        val timeDesc: String, // 23秒前发布
        @SerialName("translation_switch")
        val translationSwitch: Int // 1
    )

    @Serializable
    data class UpAction(
        @SerialName("like")
        val like: Boolean, // false
        @SerialName("reply")
        val reply: Boolean // false
    )
}


/**
 * 评论条目对象
 */
