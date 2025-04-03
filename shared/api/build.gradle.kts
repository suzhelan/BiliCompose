plugins {
    id("compose-plugin.library")
}


kotlin {
    sourceSets {

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        desktopMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.logback)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.kotlinx.serialization.protobuf)
        }
    }
}
