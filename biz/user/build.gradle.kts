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
            implementation(libs.voyager.navigator)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(projects.biz.login)
            implementation(projects.shared.common)
            implementation(projects.shared.auth)
            implementation(projects.shared.storage)
            implementation(projects.shared.api)
        }
    }

}

