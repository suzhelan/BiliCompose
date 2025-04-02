import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

//应用常用插件

plugins {
    //android模块
    id("com.android.library")
    //compose
    id("org.jetbrains.compose")
    //kotlin
    id("org.jetbrains.kotlin.plugin.compose")
    //multiplatform
    id("org.jetbrains.kotlin.multiplatform")
}
val android = extensions.findByType(LibraryExtension::class)
val composeExtension = extensions.findByType(ComposeExtension::class)

configure<KotlinMultiplatformExtension> {
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
    androidTarget {
        compilerOptions {
            jvmTarget.set(BuildVersionConfig.JVM_TARGET)
        }
    }
    sourceSets.commonMain.dependencies {
        // 添加常用依赖
        if (composeExtension != null) {
            val compose = ComposePlugin.Dependencies(project)
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
        }
    }
}

android {
    namespace = "${BuildVersionConfig.APPLICATION_ID}.${project.name}"
    compileSdk = BuildVersionConfig.COMPILE_SDK
    defaultConfig {
        minSdk = BuildVersionConfig.MIN_SDK
        testOptions.targetSdk = BuildVersionConfig.TARGET_SDK  // 控制测试目标版本
        lint.targetSdk = BuildVersionConfig.TARGET_SDK        // 控制Lint检查目标版本
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        if (composeExtension != null) {
            compose = true
        }
    }
    compileOptions {
        sourceCompatibility = BuildVersionConfig.JAVA_VERSION
        targetCompatibility = BuildVersionConfig.JAVA_VERSION
    }
}


