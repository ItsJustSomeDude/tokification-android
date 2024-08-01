import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")

}

android {
    namespace = "net.itsjustsomedude.tokens"
    compileSdk = 33

    defaultConfig {
        applicationId = "net.itsjustsomedude.tokens"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        resValue("string", "app_name", "Tokification")

        setProperty("archivesBaseName", "Tokification-v$versionCode($versionName)")

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }


    val localProps = gradleLocalProperties(rootDir)
    signingConfigs {
        create("release") {
            storeFile = file(localProps.getProperty("storeFilePath"))
            storePassword = localProps.getProperty("storePassword")
            keyPassword = localProps.getProperty("storePassword")
            keyAlias = "key1"
        }
    }
    println("Loaded local.properties signing keys.")

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

    }
}

dependencies {
    val roomVersion = "2.6.1"

    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    // To use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.preference:preference:1.2.1")
}
