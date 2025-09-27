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
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(libs.kotlinx.serialization.json)
            //尽可能只使用公共模块
            implementation(projects.shared.api)
            implementation(projects.shared.common)
            implementation(projects.shared.navigation)
        }
    }

}

