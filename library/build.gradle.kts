import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.library)
    id("maven-publish")
}

group = "com.github.dongb2002"
version = System.getenv("VERSION") ?: "1.0.0"

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                    freeCompilerArgs.add("-Xjdk-release=${JavaVersion.VERSION_1_8}")
                }
            }
        }
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "SdpSsp"
            isStatic = true
        }
    }

    jvm()

    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
        }
        androidMain.dependencies {
            implementation("com.intuit.sdp:sdp-android:1.1.1")
            implementation("com.intuit.ssp:ssp-android:1.1.1")
        }
    }
}

android {
    namespace = "com.github.dongb2002.sdpssp"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("SDP SSP Compose Multiplatform")
            description.set("Scalable DP and SP size units for Compose Multiplatform (Android, iOS, Desktop, Wasm)")
            url.set("https://github.com/dongb2002/sdp-ssp-library")
        }
    }
}
