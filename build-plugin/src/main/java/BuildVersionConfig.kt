import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/*＊ 
 * 构建配置
 ＊*/
object BuildVersionConfig {

    const val APPLICATION_ID = "top.sacz.bili"
    val JVM_TARGET = JvmTarget.JVM_17
    val JAVA_VERSION = JavaVersion.VERSION_17
    const val KOTLIN = "17"
    const val COMPILE_SDK = 35
    const val TARGET_SDK = 35
    const val MIN_SDK = 26
    const val ENABLE_IOS = true
}
