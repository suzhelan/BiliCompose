rootProject.name = "BiliCompose"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        maven { url = uri("https://maven.aliyun.com/repository/google/") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin/") }
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        maven { url = uri("https://maven.aliyun.com/repository/google/") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin/") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://jogamp.org/deployment/maven/") }
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
includeBuild("build-plugin")

include(":composeApp")
include(
    ":shared:api",
    ":shared:storage",
    ":shared:common",
    ":shared:navigation",
)
include(
    ":biz:home",
    ":biz:user",
    ":biz:login",
    ":biz:recvids")
