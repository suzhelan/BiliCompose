plugins {
    id("compose-plugin")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(libs.paging.compose.common)
            implementation(libs.coil.network.ktor3)
            implementation(libs.coil.compose)
            implementation(libs.ktor.client.core)
            implementation(compose.components.resources)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(projects.shared.api)
        }
    }

}

