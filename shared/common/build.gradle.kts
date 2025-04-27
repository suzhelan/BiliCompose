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
            implementation(projects.shared.storage)
        }
    }
}

