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
            implementation(mediampLibs.mediamp.all)
        }

        androidMain.dependencies {
            implementation(libs.androidx.media3.exoplayer)
        }

        desktopMain.dependencies {
            implementation(libs.vlcj)
        }

    }

}




