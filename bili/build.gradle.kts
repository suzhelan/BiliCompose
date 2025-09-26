plugins {
    id("compose-plugin.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            //由compose.*提供的依赖不用写,由compose-plugin.library插件处理
            //标准依赖
            implementation(libs.navigation.compose)
            //业务
            implementation(projects.biz.home)
            implementation(projects.biz.user)
            implementation(projects.biz.login)
            implementation(projects.biz.biliplayer)
            implementation(projects.biz.recvids)
            //共享通用模块
            implementation(projects.shared.storage)
            implementation(projects.shared.player)
            implementation(projects.shared.api)
            implementation(projects.shared.navigation)
        }
    }
}
