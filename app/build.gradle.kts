import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

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
//    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
}
