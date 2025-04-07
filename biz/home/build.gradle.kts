plugins {
    id("compose-plugin")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation("io.github.kevinnzou:compose-webview-multiplatform:1.9.40")
            implementation(libs.ktor.client.core)
            implementation(compose.components.resources)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.material3.adaptive.navigation.suite)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(projects.shared.api)
            implementation(projects.shared.storage)
        }
    }

}

