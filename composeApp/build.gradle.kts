import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(BuildVersionConfig.JVM_TARGET)
        }
    }

    listOf(
        iosX64(), iosArm64(), iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(projects.shared.storage)
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)
            //业务
            implementation(projects.biz.home)
            implementation(projects.biz.user)
            implementation(projects.biz.login)
            implementation(projects.biz.biliplayer)
            implementation(projects.biz.recvids)
            //共享通用模块
            implementation(projects.shared.player)
            implementation(projects.shared.api)
            implementation(projects.shared.navigation)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = BuildVersionConfig.APPLICATION_ID
    compileSdk = BuildVersionConfig.COMPILE_SDK

    defaultConfig {
        applicationId = BuildVersionConfig.APPLICATION_ID
        minSdk = BuildVersionConfig.MIN_SDK
        targetSdk = BuildVersionConfig.TARGET_SDK
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true // 必须启用
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = BuildVersionConfig.JAVA_VERSION
        targetCompatibility = BuildVersionConfig.JAVA_VERSION
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}


compose.desktop {
    application {
        mainClass = "top.sacz.bili.MainKt"

        nativeDistributions {

            nativeDistributions {
                appResourcesRootDir.set(file("appResources"))
            }

            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "BiliCompose"
            packageVersion = "1.0.0"
            description = "description"
            copyright = "© 2025 suzhelan. All rights reserved."
            vendor = "Suzhelan"
//            licenseFile.set(project.file("LICENSE.txt"))
            windows {
                shortcut = true
                perUserInstall = true
                iconFile.set(project.file("icons/icon.ico"))
                //设置编译结果路径
                outputBaseDir.set(project.file("windows"))
            }
        }
    }
}

tasks.withType<JavaExec> {
    systemProperty("compose.application.resources.dir", file("appResources").absolutePath)
}