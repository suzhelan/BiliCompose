
//应用常用插件
plugins {
    //multiplatform (kmp)
    id("org.jetbrains.kotlin.multiplatform")
    //compose
    id("org.jetbrains.compose")
    //kotlin (cmp)
    id("org.jetbrains.kotlin.plugin.compose")
    //android模块
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    android {
        namespace = "${BuildVersionConfig.APPLICATION_ID}.${project.name}"
        compileSdk = BuildVersionConfig.COMPILE_SDK
        minSdk = BuildVersionConfig.MIN_SDK
        withJava()
        androidResources {
            enable = true
        }
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget.set(BuildVersionConfig.JVM_TARGET)
            }
        }
    }

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
    jvm("desktop")
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
