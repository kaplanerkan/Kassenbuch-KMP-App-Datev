import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    // Suppress expect/actual class beta warning
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                    freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
                }
            }
        }
    }

    // ── Android target ──
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
                }
            }
        }
    }

    // ── iOS targets ──
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    // ── Desktop (JVM) target ──
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)

                // Coroutines
                implementation(libs.coroutines.core)

                // Date/Time
                implementation(libs.kotlinx.datetime)

                // Koin DI
                implementation(libs.koin.core)
                implementation(libs.koin.compose)

                // SQLDelight
                implementation(libs.sqldelight.coroutines)

                // Compose Navigation (KMP)
                implementation(libs.navigation.compose)

                // Lifecycle ViewModel (KMP)
                implementation(libs.lifecycle.viewmodel.compose.kmp)

                // Serialization (for type-safe navigation routes)
                implementation(libs.kotlinx.serialization.json)

                // Settings
                implementation(libs.multiplatform.settings)

                // Logging
                implementation(libs.kermit)

                // Image loading
                implementation(libs.coil.compose)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.coroutines.test)
            }
        }

        val androidMain by getting {
            dependencies {
                // Android-specific
                implementation(libs.core.ktx)
                implementation(libs.appcompat)
                implementation(libs.activity.compose)
                implementation(libs.lifecycle.runtime.ktx)
                implementation(libs.coroutines.android)

                // SQLDelight Android driver
                implementation(libs.sqldelight.android.driver)

                // Koin Android
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)

                // CameraX
                implementation(libs.camerax.core)
                implementation(libs.camerax.camera2)
                implementation(libs.camerax.lifecycle)
                implementation(libs.camerax.view)

                // WebView
                implementation(libs.webkit)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.sqldelight.native.driver)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.sqldelight.jvm.driver)
                implementation(libs.coroutines.swing)
            }
        }
    }
}

android {
    namespace = "de.panda.kassenbuch"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.panda.kassenbuch"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

compose.desktop {
    application {
        mainClass = "de.panda.kassenbuch.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Kassenbuch"
            packageVersion = "2.0.0"
            description = "Kassenbuch - Digitales Kassenbuch"
            vendor = "Panda Software"

            windows {
                menuGroup = "Kassenbuch"
                upgradeUuid = "b3f7c8d2-1a4e-4f9b-8c6d-2e5f7a8b9c0d"
            }

            macOS {
                bundleID = "de.panda.kassenbuch"
            }
        }
    }
}

sqldelight {
    databases {
        create("KassenbuchDatabase") {
            packageName.set("de.panda.kassenbuch.data.db")
        }
    }
}
