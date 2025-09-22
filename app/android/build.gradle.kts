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
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.compose.material3)
    implementation(projects.composeApp)
    implementation(projects.shared.storage)
}
