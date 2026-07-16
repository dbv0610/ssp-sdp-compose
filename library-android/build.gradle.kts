import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("maven-publish")
}

group = "com.sdp.ssp"
version = System.getenv("VERSION") ?: "1.2.0"

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

android {
    namespace = "com.sdp.ssp.android"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.compose.ui)
    implementation("androidx.compose.runtime:runtime:1.11.1")
    implementation(libs.androidx.window)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.sdp.ssp"
                artifactId = "android"
                pom {
                    name.set("SDP SSP Android")
                    description.set("Scalable DP and SP size units for Android Compose")
                    url.set("https://github.com/dbv0610/sdp-ssp-compose")
                }
            }
        }
    }
}
