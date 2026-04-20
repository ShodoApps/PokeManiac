// Kotlin Multiplatform presentation: shared ScreenModels / UiState (commonMain + androidTarget + iOS).
// iOS framework: ./gradlew :shared:presentation:linkDebugFrameworkIosSimulatorArm64 — see iosApp/README.md.
import co.touchlab.skie.configuration.EnumInterop
import co.touchlab.skie.configuration.FlowInterop
import co.touchlab.skie.configuration.SealedInterop
import co.touchlab.skie.configuration.SuspendInterop
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.skie)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = "PokeManiacPresentation"
            isStatic = true
            export(project(":shared:domain"))
            export(project(":shared:data"))
            export(project(":shared:api"))
            export(project(":shared:tracking"))
            export(project(":shared:database"))
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "PokeManiacPresentation"
            isStatic = true
            export(project(":shared:domain"))
            export(project(":shared:data"))
            export(project(":shared:api"))
            export(project(":shared:tracking"))
            export(project(":shared:database"))
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:domain"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.collections.immutable)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }
        iosMain.dependencies {
            api(project(":shared:data"))
            api(project(":shared:api"))
            api(project(":shared:tracking"))
            api(project(":shared:database"))
        }
    }
}

android {
    namespace = "com.shodo.android.presentation"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

skie {
    features {
        group {
            EnumInterop.Enabled(value = true)
            SealedInterop.Enabled(value = true)
            FlowInterop.Enabled(value = true)
            SuspendInterop.Enabled(value = true)
        }
    }
}
