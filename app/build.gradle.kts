plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization")
}

android {
    namespace = "com.shodo.android.pokemaniac"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shodo.android.pokemaniac"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core UI Module
    implementation(project(":coreui"))

    // Third Party Modules (for now only Tracking)
    implementation(project(":tracking"))

    // Clean Archi Module
    implementation(project(":domain"))

    // Dependency Injection - For clean archi modules
    implementation(project(":dependencyinjection"))

    // Features Modules
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:myfriends"))
    implementation(project(":feature:myprofile"))
    implementation(project(":feature:posttransaction"))
    implementation(project(":feature:searchfriend"))
    implementation(project(":feature:billing"))

    // Theming
    implementation(libs.material)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    // Koin - Dependency injection
    implementation(libs.koin.androidx.compose)

    // Coil - Remote Image Compose
    implementation(libs.coil.compose)

    // Previews
    debugImplementation(libs.androidx.compose.ui.tooling)
}