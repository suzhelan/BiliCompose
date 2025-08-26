plugins {
    id("compose-plugin.library")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(libs.paging.compose.common)
            implementation(libs.coil.network.ktor3)
            implementation(libs.coil.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.mediamp.all)

            //尽可能只使用公共模块
            implementation(projects.biz.user)
            implementation(projects.shared.api)
            implementation(projects.shared.player)
            implementation(projects.shared.common)
            implementation(projects.shared.navigation)
        }

        sourceSets.androidMain.dependencies {
            implementation(libs.androidx.media3.exoplayer)
        }


    }

}


