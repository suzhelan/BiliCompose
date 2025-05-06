plugins {
    id("compose-plugin")
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.animation)
            implementation(compose.animationGraphics)
            implementation(libs.napier)
            implementation(libs.okio)
            implementation(libs.kotlinx.datetime)
            implementation(libs.krypto)
            implementation(libs.ktor.client.core)
            implementation(projects.shared.storage)

        }
    }
}

