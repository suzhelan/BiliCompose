plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    kotlin("android")
}

android {
    namespace = BuildVersionConfig.APPLICATION_ID
    compileSdk = BuildVersionConfig.COMPILE_SDK
    defaultConfig {
        applicationId = BuildVersionConfig.APPLICATION_ID
        minSdk = BuildVersionConfig.MIN_SDK
        targetSdk = BuildVersionConfig.TARGET_SDK
        versionCode = 1
        versionName = "1.0.0"
        ndk {
            //只开放arm64 v8a,其他的等正式发布了再管
            abiFilters.add("arm64-v8a")
        }
    }
    buildFeatures {
        compose = true // 必须启用
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = BuildVersionConfig.JAVA_VERSION
        targetCompatibility = BuildVersionConfig.JAVA_VERSION
    }
    kotlin {
        jvmToolchain(BuildVersionConfig.KOTLIN.toInt())
    }
}

fun getGitCommitVersion(): String {
    return try {
        val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD").start()
        process.inputStream.bufferedReader().use { it.readText().trim() }
    } catch (e: Exception) {
        "unknown"
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach {
            val output =
                it as? com.android.build.api.variant.impl.VariantOutputImpl ?: return@forEach
            output.outputFileName.set("BiliCompose_${output.versionName.get()}-${getGitCommitVersion()}-${variant.buildType}.apk")
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.compose.material3)
    implementation(projects.bili)
}
