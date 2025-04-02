
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

//应用常用插件

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.multiplatform")
}
val composeExtension = extensions.findByType(ComposeExtension::class)

configure<KotlinMultiplatformExtension> {
    jvm()
    sourceSets.commonMain.dependencies {
        // 添加常用依赖
        if (composeExtension != null) {
            val compose = ComposePlugin.Dependencies(project)
            // Compose
            api(compose.runtime)
            api(compose.foundation)
            api(compose.animation)
            api(compose.ui)
            api(compose.material3)
            api(compose.materialIconsExtended)
        }
    }
}


