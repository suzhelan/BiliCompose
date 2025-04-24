plugins {
    id("compose-plugin.library")
    alias(libs.plugins.kotlinx.serialization)
}


kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.fastkv)
        }
        commonMain.dependencies {
            implementation(libs.multiplatform.settings)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
