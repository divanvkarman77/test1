plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.electricitycalculator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.electricitycalculator"
        minSdk = 23
        targetSdk = 35
        versionCode = 21
        versionName = "2.1"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
}
