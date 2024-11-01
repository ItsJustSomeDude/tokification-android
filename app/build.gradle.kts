@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

val localProps = gradleLocalProperties(rootDir, providers)

val versionMajor = 0
val versionMinor = 9
val versionPatch = 5
val versionBuild = 0

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.android")

    id("io.sentry.android.gradle") version "4.12.0"
    kotlin("plugin.serialization")
}

android {
    namespace = "net.itsjustsomedude.tokens"
    compileSdk = 35

    defaultConfig {
        applicationId = "net.itsjustsomedude.tokens"
        minSdk = 21
        targetSdk = 35

        versionCode = versionMajor * 10000 + versionMinor * 1000 + versionPatch * 100 + versionBuild
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"

        resValue("string", "app_name", "Tokification")

        val sentryDsn =
            localProps["sentry.dsn"]?.toString() ?: System.getenv("SENTRY_DSN") ?: run {
                println("No DSN provided, Sentry will not be enabled.")
                ""
            }

        buildConfigField("String", "SENTRY_DSN", "\"$sentryDsn\"")

        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    signingConfigs {
        create("release") {
            try {
                storeFile = file(localProps.getProperty("storeFilePath"))
                storePassword = localProps.getProperty("storePassword")
                keyPassword = localProps.getProperty("storePassword")
                keyAlias = "key1"

                // println("Loaded local.properties signing keys.")
            } catch (_: Exception) {
                // Fallback to environment variables
                try {
                    storeFile = file(
                        System.getenv("SIGNING_STORE_FILE")
                            ?: throw Error("Missing env var SIGNING_STORE_FILE")
                    )
                    storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                        ?: throw Error("Missing env var SIGNING_STORE_PASSWORD")
                    keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
                        ?: throw Error("Missing env var SIGNING_KEY_PASSWORD")
                    keyAlias = "key1"

                    // println("Loaded signing keys from environment.")
                } catch (e: Error) {
                    throw GradleException("Failed to load signing config from both local properties and environment variables: ${e.message}")
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", "Tokification")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Tokification - Debug")
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

tasks.register("printVersionName") {
    doLast {
        println(android.defaultConfig.versionName)
    }
}

dependencies {
    implementation("androidx.compose.runtime:runtime-livedata:1.7.5")
    val composeBomVersion = "2024.10.01"
    val lifecycleVersion = "2.8.7"
    val roomVersion = "2.6.1"
    val ktorVersion = "2.3.4"

    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.navigation:navigation-compose:2.8.3")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

    implementation("androidx.lifecycle:lifecycle-viewmodel-android:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // annotationProcessor("androidx.room:room-compiler:$roomVersion")
    // To use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.preference:preference-ktx:1.2.1")


    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Koin
    implementation("io.insert-koin:koin-android:4.0.0")
    implementation("io.insert-koin:koin-compose-viewmodel:4.0.0")
}
