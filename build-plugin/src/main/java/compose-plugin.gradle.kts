//应用常用插件

plugins {
    id("org.jetbrains.kotlin.plugin.compose")//composeCompiler
    id("org.jetbrains.compose")//cmp
    id("org.jetbrains.kotlin.multiplatform")//kmp
}
kotlin {

    if (BuildVersionConfig.ENABLE_IOS) {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "ComposeApp"
                isStatic = true
            }
        }
    }
    jvm()
    sourceSets.commonMain.dependencies {
        // 添加常用依赖
        // Compose
        api(compose.runtime)
        api(compose.foundation)
        api(compose.animation)
        api(compose.ui)
        api(compose.material3)
        api(compose.materialIconsExtended)
    }
}




