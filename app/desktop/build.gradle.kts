import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("jvm")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(projects.composeApp)
}


compose.desktop {
    application {
        mainClass = "top.sacz.bili.MainKt"

        nativeDistributions {

            appResourcesRootDir.set(file("appResources"))

            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "BiliCompose"
            packageVersion = "1.0.0"
            description = "description"
            copyright = "© 2025 suzhelan. All rights reserved."
            vendor = "Suzhelan"
//            licenseFile.set(project.file("LICENSE.txt"))
            windows {
                shortcut = true
                perUserInstall = true
                iconFile.set(project.file("icons/icon.ico"))
                //设置编译结果路径
                outputBaseDir.set(project.file("windows"))
            }
        }
    }
}

tasks.withType<JavaExec> {
    systemProperty("compose.application.resources.dir", file("appResources").absolutePath)
}
