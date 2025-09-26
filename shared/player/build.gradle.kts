plugins {
    id("compose-plugin.library")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.mediamp.all)
        }

        sourceSets.androidMain.dependencies {
            implementation(libs.androidx.media3.exoplayer)
        }

    }

}




