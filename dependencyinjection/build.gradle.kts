plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.shodo.android.dependencyinjection"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    implementation(project(":database"))
    implementation(project(":shared:api"))
    implementation(project(":data"))
    implementation(project(":domain"))

    // Koin - Dependency injection
    implementation(libs.koin.android)
    implementation(libs.koin.core)

    // HttpClient / HttpClientEngine types in Koin lambdas (engines come from :shared:api)
    implementation(libs.ktor.client.core)

    // Room - Database
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
