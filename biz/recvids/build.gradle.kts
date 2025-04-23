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
            implementation(projects.biz.user)
            implementation(projects.shared.api)
            implementation(projects.shared.common)
        }
    }

}

