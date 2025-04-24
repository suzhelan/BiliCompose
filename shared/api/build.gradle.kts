plugins {
    id("compose-plugin.library")
    alias(libs.plugins.kotlinx.serialization)
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
            implementation(libs.coil.network.ktor3)
            implementation(libs.coil.compose)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.protobuf)
            implementation(libs.krypto)
            //只用到个日志打印
            implementation(projects.shared.common)
            //获取登录access_key鉴权
            implementation(projects.shared.auth)
            //实现缓存
            implementation(projects.shared.storage)
        }
    }
}
