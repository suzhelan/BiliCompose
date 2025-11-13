# =========== 基础 Keep 规则 ===========
# 保留 Kotlin 元数据注解
-keepattributes Signature,InnerClasses,*Annotation*
-keepattributes Kotlin
-keepattributes Annotation
-keepattributes RuntimeVisibleAnnotations

# 保留 Kotlin Metadata 类
-keep class kotlin.Metadata { *; }

# =========== 第三方库规则 ===========

# Skia 和 Skiko 相关
-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }

# CEF 相关
-keep class org.cef.** { *; }
-keep class com.jetbrains.cef.** { *; }

# Ktor 相关
-keep class io.ktor.** { *; }
-keep class ** extends io.ktor.client.HttpClientEngineContainer { *; }

# Coroutines 相关
-keep class kotlinx.coroutines.** { *; }
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}
-keep class kotlin.coroutines.Continuation
-keep class kotlinx.coroutines.CancellableContinuation
-keep class kotlinx.coroutines.channels.Channel
-keep class kotlinx.coroutines.CoroutineDispatcher
-keep class kotlinx.coroutines.CoroutineScope

# Serialization 相关
-keep class kotlinx.serialization.json.** { *; }
-keep @kotlinx.serialization.Serializable class * {*;}

# 日志框架相关
-keep class org.slf4j.** { *; }
-keep class org.slf4j2.** { *; }
-keep class org.apache.logging.log4j.** { *; }
-keep class ** extends org.slf4j.spi.SLF4JServiceProvider { *; }
-keep class org.apache.logging.slf4j.SLF4JServiceProvider { *; }

# Coil 图片加载库
-keep class coil3.** { *; }

# DataStore 相关
-keep class androidx.datastore.** { *; }

# Thrift 相关
-keep class org.apache.thrift.** { *; }

# =========== 平台和系统相关 ===========

# Swing 相关
-keep class kotlinx.coroutines.swing.SwingDispatcherFactory

# JNA 相关（Native绑定）
-keep class uk.co.caprica.vlcj.** { *; }
-keep class com.sun.jna.** { *; }
-keep class ** extends com.sun.jna.Structure { *; }
-keep class ** extends com.sun.jna.Library { *; }
-keep enum com.sun.jna.** { *; }

# 系统相关
-keep class sun.misc.Unsafe { *; }
-keep class oshi.** { *; }
-keep class com.jthemedetecor.** { *; }

# =========== Compose 相关 ===========

# Compose 运行时
-keep class androidx.compose.runtime.SnapshotStateKt__DerivedStateKt { *; }
-keep class androidx.compose.runtime.ComposerKt {
    void sourceInformation(androidx.compose.runtime.Composer,java.lang.String);
    void sourceInformationMarkerStart(androidx.compose.runtime.Composer,int,java.lang.String);
    void sourceInformationMarkerEnd(androidx.compose.runtime.Composer);
    boolean isTraceInProgress();
    void traceEventStart(int, java.lang.String);
    void traceEventEnd();
}

# Compose UI 场景相关
-keep class androidx.compose.ui.scene.PlatformLayersComposeSceneImpl { *; }
-keep class androidx.compose.ui.scene.CanvasLayersComposeSceneImpl { *; }
-keep class androidx.compose.ui.scene.CanvasLayersComposeSceneImpl$AttachedComposeSceneLayer { *; }

# 被反射访问的 Compose 类
-keep class androidx.compose.foundation.HoverableNode { *; }
-keep class androidx.compose.foundation.gestures.ScrollableNode { *; }

# 反射访问的字段和方法
-keepclassmembers class androidx.compose.ui.scene.PlatformLayersComposeSceneImpl {
    private *** getMainOwner();
}

-keepclassmembers class androidx.compose.ui.scene.CanvasLayersComposeSceneImpl {
    private *** mainOwner;
    private *** _layersCopyCache;
    private *** focusedLayer;
}

-keepclassmembers class androidx.compose.ui.scene.CanvasLayersComposeSceneImpl$AttachedComposeSceneLayer {
    private *** owner;
    private *** isInBounds(...);
}

# =========== SQLite 相关 ===========
-keep class androidx.sqlite.driver.bundled.** { *; }

# =========== 忽略警告规则 ===========

# Coroutines 相关警告
-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.SignalHandler
-dontwarn java.lang.instrument.Instrumentation
-dontwarn sun.misc.Signal
-dontwarn java.lang.ClassValue
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn android.annotation.SuppressLint
-dontwarn kotlinx.coroutines.debug.internal.**
-dontwarn kotlinx.coroutines.internal.**

# 其他警告
-dontwarn aQute.bnd.**
-dontwarn okhttp3.internal.**
-dontwarn reactor.blockhound.**
-dontwarn com.ctc.wstx.**
-dontwarn com.lmax.disruptor.**
-dontwarn com.sun.jna.internal.**
-dontnote kotlin.coroutines.jvm.internal.**
-dontnote kotlin.internal.**
-dontnote kotlin.jvm.internal.**
-dontnote kotlin.reflect.**
-dontnote kotlinx.coroutines.debug.internal.**
-dontnote kotlinx.coroutines.internal.**
-dontnote com.sun.javafx.**
-dontnote kotlin.reflect.jvm.internal.**

# =========== 其他规则 ===========

# 用户自定义接口实现
-keep class ** implements org.openani.mediamp.MediampPlayerFactory { *; }

# 服务加载器相关
-keep class ** extends uk.co.caprica.vlcj.factory.discovery.provider.DiscoveryDirectoryProvider { *; }

# Kotlin 反射相关
-keep class kotlin.reflect.jvm.internal.** { *; }

# okio VerifyError 相关
-keep class okio.Okio__JvmOkioKt { *; }
-keep class okio.Okio__OkioKt { *; }
-keep class okio.**

# 其他配置
-verbose
-dontpreverify
