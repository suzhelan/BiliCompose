plugins {
    id("compose-plugin")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(libs.material3.adaptive.navigation.suite)
            implementation(projects.biz.login)
            implementation(projects.biz.recvids)
            implementation(libs.voyager.navigator)
        }
    }

}

