plugins {
    id("compose-plugin.library")
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.animation)
            implementation(compose.animationGraphics)
            implementation(libs.paging.compose.common)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.napier)
            implementation(libs.okio)
            implementation(libs.kotlinx.datetime)
            implementation(libs.krypto)
            implementation(libs.ktor.client.core)
        }
    }
}

