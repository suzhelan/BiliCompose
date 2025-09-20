import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(libs.android.gradle.plugin)
    implementation(libs.android.application.gradle.plugin)
    implementation(libs.android.library.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.multiplatfrom.gradle.plugin)
    implementation(libs.kotlin.compose.compiler.gradle.plugin)
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

