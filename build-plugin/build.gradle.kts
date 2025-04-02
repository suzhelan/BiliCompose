import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
    `kotlin-dsl`
}

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    maven { url = uri("https://maven.aliyun.com/repository/google/") }
    maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin/") }
    maven { url = uri("https://jitpack.io") }
    google()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    api(gradleApi())
    api(gradleKotlinDsl())
    api(libs.android.gradle.plugin)
    api(libs.android.application.gradle.plugin)
    api(libs.android.library.gradle.plugin)
    api(libs.kotlin.gradle.plugin)
    api(libs.compose.multiplatfrom.gradle.plugin)
    api(libs.kotlin.compose.compiler.gradle.plugin)
}

java {
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

