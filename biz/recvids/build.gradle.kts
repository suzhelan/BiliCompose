plugins {
    id("compose-plugin.library")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {

        commonMain.dependencies {
            implementation(libs.paging.compose.common)
            implementation(libs.coil.network.ktor3)
            implementation(libs.coil.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.biz.user)
            implementation(projects.shared.api)
            implementation(libs.voyager.navigator)
            implementation(projects.shared.common)
            implementation(projects.shared.navigation)
        }
    }

}

