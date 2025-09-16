
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
        withJava() // enable java compilation support
        androidResources {
            enable = true
        }
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget.set(BuildVersionConfig.JVM_TARGET)
            }
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
            implementation(compose.runtime)
            implementation(compose.foundation)
            //动画
            implementation(compose.animation)
            //UI
            implementation(compose.ui)
            //material3主题系列控件 m3.material.io
            implementation(compose.material3)
            //图标拓展 可在https://fonts.google.com/icons找到许多图标
            implementation(compose.materialIconsExtended)
            //res
            implementation(compose.components.resources)
        }
    }
}
