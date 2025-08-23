## 基于 ComposeMultiplatform(CMP)+KotlinMultiplatform(KMP) 的BiliBili 哔哩哔哩 跨平台客户端

个人练习使用 持续维护中

### 已适配的平台
- [x] Android
- [ ] Win (尚未完全适配)
- [ ] Mac
- [ ] Ios
- [ ] Linux
### 已实现的功能
- [x] 登录功能
- [x] 主页功能
- [x] 主页推荐视频
- [x] 主页热门视频
- [x] 手机号登录
- [x] 扫码登录

### 等待实现的功能
- [ ] 播放器(在做)
- [ ] 搜索功能
- [ ] 直播功能
- [ ] 番剧功能
- [ ] 专栏功能
- [ ] 播放器
- [ ] 评论功能
- [ ] 个人主页和动态
- [ ] 密码登录(容易风控导致仍然需要验证码登录，暂时不做)

### 正在解决的问题
- [x] 播放器音频流和视频流无法声画同步(Android平台解决)
- [ ] 解决Desktop Player的问题(Install VLC, Video Audio Sync)
- [ ] 解决Desktop Webview无法使用的问题(现有方案KCEF在Desktop上无法正常运行) 受到影响的功能: 登录业务-极验人机验证
