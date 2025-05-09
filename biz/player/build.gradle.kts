plugins {
    id("compose-plugin.library")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation("org.openani.mediamp:mediamp-all:0.0.23")
//            implementation("network.chaintech:compose-multiplatform-media-player:1.0.40")
            implementation(libs.paging.compose.common)
            implementation(libs.coil.network.ktor3)
            implementation(libs.coil.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.shared.api)
            implementation(projects.shared.common)
            implementation(libs.voyager.navigator)
        }
    }

}

