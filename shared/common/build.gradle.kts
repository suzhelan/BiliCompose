plugins {
    id("compose-plugin.library")
}

/**
 * 不要依赖任何projects的模块 保持低耦合快速迁移到其他项目
 */
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.animation)
            implementation(compose.animationGraphics)
            implementation(libs.paging.compose)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.napier)
            implementation(libs.okio)
            implementation(libs.kotlinx.datetime)
            implementation(libs.krypto)
            implementation(libs.ktor.client.core)
            implementation(libs.coil.compose)
        }
    }
}

