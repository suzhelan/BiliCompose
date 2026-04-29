//应用常用插件

plugins {
    id("org.jetbrains.kotlin.plugin.compose")//composeCompiler
    id("org.jetbrains.compose")//cmp
    id("org.jetbrains.kotlin.multiplatform")//kmp
}
kotlin {

    if (BuildVersionConfig.ENABLE_IOS) {
        listOf(
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
    sourceSets {
        //noinspection UseTomlInstead
        commonMain.dependencies {
            // 添加常用依赖
            // Compose
            implementation("org.jetbrains.compose.runtime:runtime:1.10.3")
            implementation("org.jetbrains.compose.foundation:foundation:1.10.3")
            implementation("org.jetbrains.compose.animation:animation:1.10.3")
            implementation("org.jetbrains.compose.ui:ui:1.10.3")
            implementation("org.jetbrains.compose.material3:material3:1.9.0")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
            implementation("org.jetbrains.compose.components:components-resources:1.10.3")
        }
    }
}




