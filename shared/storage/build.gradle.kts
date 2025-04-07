plugins {
    id("compose-plugin.library")
    alias(libs.plugins.kotlinx.serialization)
}


kotlin {
    sourceSets {

        commonMain.dependencies {
            implementation(libs.multiplatform.settings)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
