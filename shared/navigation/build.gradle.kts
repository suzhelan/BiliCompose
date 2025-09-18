plugins {
    id("compose-plugin.library")
    alias(libs.plugins.kotlinx.serialization)
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            //注意!! 此模块不能引用任何其他的业务模块 否则可能会造成相互依赖冲突
            implementation(libs.navigation.compose)
        }
    }
}