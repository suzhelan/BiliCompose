plugins {
    id("compose-plugin")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation("com.squareup.okio:okio:3.10.2")
            implementation(libs.ktor.client.core)
            implementation(compose.components.resources)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.material3.adaptive.navigation.suite)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(projects.shared.api)
        }
    }

}

