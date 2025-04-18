plugins {
    id("compose-plugin")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(projects.biz.login)
            implementation(projects.biz.user)
            implementation(projects.biz.recvids)
            implementation(libs.voyager.navigator)
        }
    }

}

