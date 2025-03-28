plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.universalyogaadmin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.universalyogaadmin"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Xóa các dependency liên quan đến Kotlin
    // implementation(libs.androidx.core.ktx)
    // implementation(libs.androidx.lifecycle.runtime.ktx)

    // Thay thế bằng các dependency Java
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.fragment:fragment:1.8.2")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}