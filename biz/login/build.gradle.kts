plugins {
    id("compose-plugin.library")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(BuildVersionConfig.JVM_TARGET)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.webview.multiplatform)
            implementation(libs.ktor.client.core)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.sonner)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(libs.voyager.navigator)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.shared.navigation)
            implementation(projects.shared.api)
            implementation(projects.shared.storage)
            implementation(projects.shared.common)
        }
    }

}
