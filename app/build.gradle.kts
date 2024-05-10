import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")

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

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    val keystoreProperties = Properties()
    keystoreProperties.load(FileInputStream("signing.properties"))

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties.getProperty("storeFilePath"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            keyAlias = "key1"
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

    }
}

dependencies {
//    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
}
