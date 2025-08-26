plugins {
    id("compose-plugin")
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            //注意!! 此模块不能引用任何其他的模块 否则可能会造成相互依赖冲突
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
        }
    }
}
