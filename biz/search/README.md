# 搜索

首页 → 搜索输入页（历史、热搜、联想）→ 视频 / 用户搜索结果 → 播放页 / 用户主页。

- 输入联想防抖 300ms；提交、联想、历史和热搜统一进入结果页。
- 历史本地保存最近 30 条，支持删除和清空。
- 视频支持排序和时长筛选，用户支持默认排序和粉丝排序。
- 每类结果按需加载，独立缓存分页和滚动位置；改变筛选取消旧分页请求。
- 首次加载显示圆形进度，接口确认空结果后显示空态；翻页失败可原位重试。
- Web 请求由 `shared:api/BiliWebClient` 处理游客 Cookie、登录 Cookie 和 WBI，独立于 App 请求配置。
- 暂不包含综合混排、番剧、影视、直播和下拉刷新。

## 构建

```powershell
.\gradlew.bat :app:android:assembleDebug :app:desktop:compileKotlin
```
