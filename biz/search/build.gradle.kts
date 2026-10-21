plugins {
    id("compose-plugin.library")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.paging.compose)
            implementation(libs.coil.compose)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.shared.api)
            implementation(projects.shared.common)
            implementation(projects.shared.navigation)
            implementation(projects.shared.storage)
        }
    }
}
