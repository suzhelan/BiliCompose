plugins {
    id("compose-plugin")
    alias(libs.plugins.kotlinx.serialization)
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.shared.storage)
        }
    }
}
