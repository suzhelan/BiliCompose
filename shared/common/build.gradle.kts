plugins {
    id("compose-plugin")
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.animation)
            implementation(compose.animationGraphics)
            implementation(libs.napier)
            implementation("com.squareup.okio:okio:3.11.0")
            implementation(projects.shared.storage)
        }
    }
}
