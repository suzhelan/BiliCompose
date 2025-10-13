plugins {
    id("compose-plugin.library")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(libs.paging.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.coil.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.mediamp.all)
            implementation(projects.biz.user)
            implementation(projects.biz.biliplayer)
            implementation(projects.biz.recvids)
            implementation(projects.shared.auth)
            implementation(projects.shared.api)
            implementation(projects.shared.common)
            implementation(projects.shared.navigation)
            implementation(projects.shared.player)
        }
    }
}
