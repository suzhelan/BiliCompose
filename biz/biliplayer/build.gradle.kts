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
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(libs.kotlinx.serialization.json)
            implementation("org.jetbrains.compose.material3.adaptive:adaptive:1.2.0-alpha06")
            implementation("org.jetbrains.compose.material3.adaptive:adaptive-layout:1.2.0-alpha06")
            implementation("org.jetbrains.compose.material3.adaptive:adaptive-navigation:1.2.0-alpha06")
            //尽可能只使用公共模块
            implementation(projects.biz.user)
            implementation(projects.shared.api)
            implementation(projects.shared.player)
            implementation(projects.shared.common)
            implementation(projects.shared.navigation)
        }
    }

}


