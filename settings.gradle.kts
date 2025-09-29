rootProject.name = "BiliCompose"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
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
}
includeBuild("build-plugin")

include(":bili")
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
    ":biz:recvids",
    ":biz:comment"
)
include(":app:android", ":app:desktop")

includeBuild("../mediamp") {
    dependencySubstitution {
        substitute(module("org.openani.mediamp:mediamp-api"))
            .using(project(":mediamp-api"))
        substitute(module("org.openani.mediamp:mediamp-exoplayer"))
            .using(project(":mediamp-exoplayer"))
        substitute(module("org.openani.mediamp:mediamp-vlc"))
            .using(project(":mediamp-vlc"))
        substitute(module("org.openani.mediamp:mediamp-mpv"))
            .using(project(":mediamp-mpv"))
        substitute(module("org.openani.mediamp:mediamp-source-ktxio"))
            .using(project(":mediamp-source-ktxio"))
        substitute(module("org.openani.mediamp:mediamp-avkit"))
            .using(project(":mediamp-avkit"))
        substitute(module("org.openani.mediamp:mediamp-all"))
            .using(project(":mediamp-all"))
    }
}
