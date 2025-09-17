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
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.animation)
        implementation(compose.ui)
        implementation(compose.material3)
        implementation(compose.materialIconsExtended)
        implementation(compose.components.resources)
    }
}




