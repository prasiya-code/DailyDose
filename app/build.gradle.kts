plugins {
    // 1. Core Android Application Plugin
    id("com.android.application")
    // 2. Kotlin Android Plugin for Kotlin support
    id("org.jetbrains.kotlin.android")
    // 3. Kotlin Kapt/KSP if you use Room/Hilt (optional, uncomment if needed)
    // id("kotlin-kapt")
}

android {
    namespace = "com.example.dailydose"
    compileSdk = 34 // Use the latest stable SDK version

    defaultConfig {
        applicationId = "com.example.dailydose"
        minSdk = 24 // Minimum supported Android version
        targetSdk = 34 // Target the latest stable SDK version
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Standard for modern Android
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // CRITICAL BLOCK FOR VIEW BINDING
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // -------------------------------------------------------------------------
    // CORE ANDROID & KOTLIN
    // -------------------------------------------------------------------------
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

    // -------------------------------------------------------------------------
    // UI LIBRARIES
    // -------------------------------------------------------------------------
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // GRID LAYOUT (FIXES 'cannot find symbol class GridLayout' error)
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // FRAGMENTS
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // -------------------------------------------------------------------------
    // NAVIGATION & LIFECYCLE
    // -------------------------------------------------------------------------
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // -------------------------------------------------------------------------
    // UTILITIES (FIXES GSON AND WORKMANAGER ERRORS)
    // -------------------------------------------------------------------------
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // -------------------------------------------------------------------------
    // TESTING
    // -------------------------------------------------------------------------
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
