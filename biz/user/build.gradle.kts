plugins {
    id("compose-plugin")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(projects.shared.storage)
            implementation(projects.shared.api)
        }
    }

}

