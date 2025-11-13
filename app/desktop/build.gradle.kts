import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("jvm")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(projects.bili)
}

kotlin {
    jvmToolchain(BuildVersionConfig.KOTLIN.toInt())
}

compose.desktop {
    application {
        mainClass = "top.suzhelan.bili.MainKt"

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


        buildTypes.release.proguard {
            isEnabled.set(true)
            version = "7.8.1"
            optimize.set(true)
            obfuscate.set(false)
            this.configurationFiles.from(file("proguard-desktop.pro"))
        }

    }
}
afterEvaluate {
    tasks.withType<JavaExec> {
        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.desktop/java.awt.peer=ALL-UNNAMED")

        if (System.getProperty("os.name").contains("Mac")) {
            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
        }
    }
}
tasks.withType<JavaExec> {
    systemProperty("compose.application.resources.dir", file("appResources").absolutePath)
}
