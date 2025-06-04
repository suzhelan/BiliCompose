
//应用常用插件

plugins {
    //multiplatform (kmp)
    id("org.jetbrains.kotlin.multiplatform")
    //android模块
    id("com.android.library")
    //compose
    id("org.jetbrains.compose")
    //kotlin (cmp)
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(BuildVersionConfig.JVM_TARGET)
        }
    }
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
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            // 添加常用依赖
            // Compose
            api(compose.runtime)
            api(compose.foundation)
            //动画
            api(compose.animation)
            //UI
            api(compose.ui)
            //material3主题系列控件 m3.material.io
            api(compose.material3)
            //图标拓展 可在https://fonts.google.com/icons找到许多图标
            api(compose.materialIconsExtended)
            //res
            api(compose.components.resources)
        }
    }
}


android {
    namespace = "${BuildVersionConfig.APPLICATION_ID}.${project.name}"
    compileSdk = BuildVersionConfig.COMPILE_SDK
    defaultConfig {
        minSdk = BuildVersionConfig.MIN_SDK
    }

    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = BuildVersionConfig.JAVA_VERSION
        targetCompatibility = BuildVersionConfig.JAVA_VERSION
    }
}


