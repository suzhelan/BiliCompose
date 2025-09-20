plugins {
    id("compose-plugin.library")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.webview.multiplatform)
            implementation(libs.coil.network.ktor3)
            implementation(libs.coil.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.sonner)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.qrose)

            implementation(projects.shared.auth)
            implementation(projects.shared.navigation)
            implementation(projects.shared.api)
            implementation(projects.shared.storage)
            implementation(projects.shared.common)
        }
    }

}
