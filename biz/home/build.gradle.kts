plugins {
    id("compose-plugin")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coil.network.ktor3)
            implementation(libs.coil.compose)
            implementation(libs.ktor.client.core)
            implementation(compose.components.resources)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.material3.adaptive.navigation.suite)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(projects.biz.login)
            implementation(projects.shared.api)
            implementation(projects.shared.storage)
        }
    }

}

