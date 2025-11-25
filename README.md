## 基于 ComposeMultiplatform(CMP)+KotlinMultiplatform(KMP) 的BiliBili 哔哩哔哩 跨平台客户端

个人练习使用 持续维护中

当前进度,更多内容可在您自行构建运行后查看,等待将来完成常用功能后会发布release,这或许不会很久

在这之前 你可以通过Action中的自动构建来查阅每夜版本(Android)

在手机(Android)
<img width="1166" height="632" alt="IMG_20250920_202709" src="https://github.com/user-attachments/assets/3adb910d-13ea-41ce-b9d5-2e5197312e1a" />

在Desktop(Win)
<img width="1589" height="1199" alt="image" src="https://github.com/user-attachments/assets/6c3c0497-2ab8-40d7-b7a1-727473c31e5a" />
<img width="1992" height="1063" alt="image" src="https://github.com/user-attachments/assets/6fed6603-7840-48be-99d8-75dafc1ba926" />


### 已适配的平台
- [x] Android
- [x] Win
- [x] Linux(推进中)
- [x] Mac(推进中)
- [ ] Ios
- [ ] Web
### 已实现的功能
- [x] 登录功能
- [x] 主页功能
- [x] 主页推荐视频
- [x] 主页热门视频
- [x] 手机号登录
- [x] 扫码登录
- [x] 视频播放,点赞投币等
- [x] 关注操作(关注,取关)分组添加设置删除等

### 等待实现的功能
- [x] 我的页(基本指标已实现)
- [x] 视频播放器(基本操作功能已实现)
- [x] 评论区(实现中)
- [x] 竖屏短视频(基本功能已实现)
- [x] 个人资料主页(开始推动)
- [ ] 搜索功能
- [ ] 直播功能
- [ ] 番剧功能
- [ ] 专栏功能
- [ ] 视频收藏
- [ ] 私聊消息
- [ ] 安卓画中画

### 正在解决的问题 目前困难问题已经全部解决！
- [x] 播放器音频流和视频流无法声画同步(Android平台解决)
- [x] 解决Desktop Player音频流和视频流同步的问题(VLC VideoUrl AudioUrl Sync)，哔哩哔哩的视频使用的是音频链接+视频链接的视频流组合，目前VLCJ还没有找到可靠的方法来实现合并同步
- [x] 解决Desktop Webview无法使用的问题(现有方案KCEF在Desktop上无法正常运行) 受到影响的功能: 登录业务-极验人机验证

## 项目架构

接口来自个人抓取与[bilibili-API-collect](https://github.com/SocialSisterYi/bilibili-API-collect)公开的接口

### 技术栈 
这些开源项目非常的伟大 他们为ComposeMultiplatform提供了不可代替的作用
- 网络请求:[ktor](https://github.com/ktorio/ktor)
- 导航:封装+[jetbrains navigation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation.html)
- 视频播放:[mediamp](https://github.com/open-ani/mediamp)
- 图片加载:[coil3](https://github.com/coil-kt/coil)
- 视频流媒体播放器:[mediamp](https://github.com/open-ani/mediamp)->android使用ExoPlayer,Desktop使用
- 持久化存储:[multiplatform-settings](https://github.com/russhwolf/multiplatform-settings)
- 登录时的二维码生成:[qr-kit](https://github.com/Chaintech-Network/QRKitComposeMultiplatform)
- 加解密编码(后续需要使用更加活跃的工具):[krypto](https://mvnrepository.com/artifact/com.soywiz.korlibs.krypto/krypto)
- WebView+极验人机验证:[geetest](./biz/login/src/commonMain/composeResources/files/geetest-lite.html)
- 跨平台分页加载:
  ~~[cashapp-paging](https://github.com/cashapp/multiplatform-paging)~~,[google-paging-compose](https://developer.android.com/jetpack/androidx/releases/paging?hl=zh-cn#3.4.0-alpha04)
- IO读写:[okio](https://github.com/square/okio)
- 多平台日志读写:[napier](https://github.com/AAkira/Napier)
- WebView:[kevinnzou:compose-webview-multiplatform](https://github.com/KevinnZou/compose-webview-multiplatform)
- 序列化,包含json,protobuf等,使用:[kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)


### 模块划分

开发均需要在合适的模块进行开发,保持低耦合度与高性能

 1.业务模块 [biz](./biz) 其中的模块包含 
- 登录
- 用户
- 首页
- 视频推荐
- 视频播放  

 2.共享模块 [shared](./shared),其中包含通用常用的可以轻松的移植到到其他项目,
 - 例如[navigation](./shared/navigation),负责各个Screen之间的导航
 - 常用的[common](./shared/common),其中包含BaseViewModel,LogUtils,各种扩展以及常用UI,Color,如骨架屏,,Dialog等
 - 存储[storage](./shared/storage)
 - 保存登录凭证[auth](./shared/auth)
 - 网络请求[api](./shared/api)
 - 播放器[player](./shared/player)

 3.bili模块
 - 核心本体模块,负责收拢所有导航路由和Screen,定义AppTheme等

### 项目风格
 1.项目采用MVVM风格的编程模式,实现一个功能需要实现以下
 - Screen 用于实现本页面的UI
 - ViewModel 用于处理本页面的数据逻辑
 - Api 用于定义本页面需要使用的网络请求
 - Entity 需要使用到的请求或交互数据实体

 2.想要构建一个页面(screen/activity/page)需要完成以下三步
 - 创建[XXXScreen](./biz/home/src/commonMain/kotlin/top/suzhelan/bili/biz/home/HomeScreen.kt)方法
 - 在[Navigation]( ./shared/navigation)中SharedScreen声明路由
 - 在[RouteScreenRegistry](./composeApp/src/commonMain/kotlin/top/sacz/bili/route/RouteScreenRegistry.kt)注册路由和Screen
