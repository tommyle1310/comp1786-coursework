plugins {
    alias(libs.plugins.android.application)
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.fragment:fragment:1.8.2") // Thêm dependency cho Fragment
    implementation("androidx.recyclerview:recyclerview:1.3.2") // Thêm dependency cho RecyclerView
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}