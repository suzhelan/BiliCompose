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
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.constraintlayout.compose.multiplatform)
            //尽可能只引用公共模块，避免引用不必要的模块
            implementation(projects.biz.base)
            implementation(projects.biz.login)
            implementation(projects.shared.common)
            implementation(projects.shared.auth)
            implementation(projects.shared.storage)
            implementation(projects.shared.api)
            implementation(projects.shared.navigation)
        }
    }

}

