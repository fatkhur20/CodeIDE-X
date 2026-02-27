plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.codeide.x"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.codeide.x"
        minSdk = 26
        targetSdk = 34
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // EditorKit
    implementation("com.blacksquircle.ui:editorkit:2.9.0")
    implementation("com.blacksquircle.ui:language-java:2.9.0")
    implementation("com.blacksquircle.ui:language-kotlin:2.9.0")
    implementation("com.blacksquircle.ui:language-python:2.9.0")
    implementation("com.blacksquircle.ui:language-javascript:2.9.0")
    implementation("com.blacksquircle.ui:language-typescript:2.9.0")
    implementation("com.blacksquircle.ui:language-html:2.9.0")
    implementation("com.blacksquircle.ui:language-css:2.9.0")
    implementation("com.blacksquircle.ui:language-json:2.9.0")
    implementation("com.blacksquircle.ui:language-xml:2.9.0")
    implementation("com.blacksquircle.ui:language-go:2.9.0")
    implementation("com.blacksquircle.ui:language-rust:2.9.0")
    implementation("com.blacksquircle.ui:language-c:2.9.0")
    implementation("com.blacksquircle.ui:language-cpp:2.9.0")
    implementation("com.blacksquircle.ui:language-plaintext:2.9.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}