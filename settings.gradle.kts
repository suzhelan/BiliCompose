rootProject.name = "BiliCompose"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://jitpack.io") }
        //webview
        maven("https://jogamp.org/deployment/maven")
    }
    versionCatalogs {
        create("mediampLibs") {
            from("org.openani.mediamp:catalog:0.0.29")
        }
    }
}
includeBuild("build-plugin")

include(":composeApp")
include(
    ":shared:api",
    ":shared:storage",
    ":shared:common",
    ":shared:navigation",
    ":shared:auth",
    ":shared:player",
)
include(
    ":biz:home",
    ":biz:biliplayer",
    ":biz:user",
    ":biz:login",
    ":biz:recvids"
)