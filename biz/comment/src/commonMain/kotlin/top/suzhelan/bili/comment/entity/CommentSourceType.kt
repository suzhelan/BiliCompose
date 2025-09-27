package top.suzhelan.bili.comment.entity

/**
 * 评论区类型枚举
 * @param source 类型名称
 * @param type 类型编码
 *
 * 代码	评论区类型	oid 的意义
 * 1	视频稿件	稿件 avid
 * 2	话题	话题 id
 * 4	活动	活动 id
 * 5	小视频	小视频 id
 * 6	小黑屋封禁信息	封禁公示 id
 * 7	公告信息	公告 id
 * 8	直播活动	直播间 id
 * 9	活动稿件	(?)
 * 10	直播公告	(?)
 * 11	相簿（图片动态）	相簿 id
 * 12	专栏	专栏 cvid
 * 13	票务	(?)
 * 14	音频	音频 auid
 * 15	风纪委员会	众裁项目 id
 * 16	点评	(?)
 * 17	动态（纯文字动态&分享）	动态 id
 * 18	播单	(?)
 * 19	音乐播单	(?)
 * 20	漫画	(?)
 * 21	漫画	(?)
 * 22	漫画	漫画 mcid
 * 33	课程	课程 epid
 */
enum class CommentSourceType(val source: String, val type: Int) {
    /**
     * 视频稿件 - 稿件 avid
     */
    Video("视频", 1),

    /**
     * 话题 - 话题 id
     */
    Topic("话题", 2),

    /**
     * 活动 - 活动 id
     */
    Activity("活动", 4),

    /**
     * 小视频 - 小视频 id
     */
    SmallVideo("小视频", 5),

    /**
     * 小黑屋封禁信息 - 封禁公示 id
     */
    BlackRoom("小黑屋封禁信息", 6),

    /**
     * 公告信息 - 公告 id
     */
    Notice("公告信息", 7),

    /**
     * 直播活动 - 直播间 id
     */
    LiveActivity("直播活动", 8),

    /**
     * 活动稿件 - (?)
     */
    ActivityVideo("活动稿件", 9),

    /**
     * 直播公告 - (?)
     */
    LiveNotice("直播公告", 10),

    /**
     * 相簿（图片动态） - 相簿 id
     */
    Album("相簿（图片动态）", 11),

    /**
     * 专栏 - 专栏 cvid
     */
    Article("专栏", 12),

    /**
     * 票务 - (?)
     */
    Ticket("票务", 13),

    /**
     * 音频 - 音频 auid
     */
    Audio("音频", 14),

    /**
     * 风纪委员会 - 众裁项目 id
     */
    Jury("风纪委员会", 15),

    /**
     * 点评 - (?)
     */
    Review("点评", 16),

    /**
     * 动态（纯文字动态&分享） - 动态 id
     */
    Dynamic("动态（纯文字动态&分享）", 17),

    /**
     * 播单 - (?)
     */
    Playlist("播单", 18),

    /**
     * 音乐播单 - (?)
     */
    MusicPlaylist("音乐播单", 19),

    /**
     * 漫画 - (?)
     */
    Comic1("漫画", 20),

    /**
     * 漫画 - (?)
     */
    Comic2("漫画", 21),

    /**
     * 漫画 - 漫画 mcid
     */
    Comic3("漫画", 22),

    /**
     * 课程 - 课程 epid
     */
    Course("课程", 33)
}
