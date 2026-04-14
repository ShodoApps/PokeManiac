plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.shodo.android.pokemaniac"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.shodo.android.pokemaniac"
        minSdk = 26
        targetSdk = 36
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core UI Module
    implementation(project(":coreui"))

    // Third Party Modules (for now only Tracking)
    implementation(project(":shared:tracking"))

    // Clean Archi Module
    implementation(project(":shared:domain"))

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

    // Coil 3: compose UI + explicit OkHttp network fetcher (required for https:// image URLs; not bundled in coil-compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    // Previews
    debugImplementation(libs.androidx.compose.ui.tooling)
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
