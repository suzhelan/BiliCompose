plugins {
    id("compose-plugin")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.webview.multiplatform)
            implementation(libs.ktor.client.core)
            implementation(compose.components.resources)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.sonner)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(projects.shared.api)
            implementation(projects.shared.common)
        }
    }

}

