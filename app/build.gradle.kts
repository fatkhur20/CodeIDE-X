plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.codeide.x"
    compileSdk = 33
    buildToolsVersion = "33.0.3"

    defaultConfig {
        applicationId = "com.codeide.x"
        minSdk = 26
        targetSdk = 33
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.8"
    }
}

dependencies {
    // Compose BOM - older version for SDK 33
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.navigation:navigation-compose:2.6.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}