plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

val apiToken = System.getenv("SUPER_HERO_API_TOKEN") ?: ""

android {
    namespace = "com.shodo.android.api"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "apiToken", "\"${apiToken}\"")
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

    android.buildFeatures.buildConfig = true
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    // Coroutines Core - Flow
    implementation(libs.kotlinx.coroutines.core)

    // Koin - Dependency injection
    implementation(libs.koin.core)

    // SquareUp Retrofit & OkHttp - Network (BOM pins OkHttp 5.x; Retrofit 3 otherwise pulls OkHttp 4.12)
    implementation(platform(libs.squareup.okhttp.bom))
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.okhttp.brotli)
    implementation(libs.squareup.retrofit.converter.gson)
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
