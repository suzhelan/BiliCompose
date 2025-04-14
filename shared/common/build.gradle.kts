plugins {
    id("compose-plugin")
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.napier)
        }
    }
}
