// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // Xóa alias(libs.plugins.kotlin.compose) vì không dùng Compose trong Admin App
}