
import co.touchlab.skie.configuration.SealedInterop
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.skie)
    alias(libs.plugins.mavenDeployer)
}

group = "io.stepuplabs.spaydkmp"
version = System.getenv("GITHUB_RELEASE_VERSION") ?: "SNAPSHOT"

kotlin {
    androidTarget {
    }

    val xcf = XCFramework("spaydkmp")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "spaydkmp"
            isStatic = true

            xcf.add(this)
        }
    }

    jvm()

    js {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
        d8()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.http)
            implementation(libs.okio)
            implementation(libs.urlencoder)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.junit)
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.test.annotations)
        }
    }
}

android {
    namespace = "io.stepuplabs.spaydkmp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    publishing {
        singleVariant("release")
    }
}

skie {
    features {
        group {
            SealedInterop.Enabled(true)
        }
    }
}

deployer {
    content {
        kotlinComponents {
            emptyDocs()
        }
    }
    projectInfo {
        artifactId = "spayd-kmp"
        description = "Kotlin Multiplatform library for generating Short Payment Descriptor (SPAYD)."
        url = "https://github.com/step-up-labs/spayd-kmp"
        scm.fromGithub("step-up-labs", "spayd-kmp")
        license(MIT)
        developer("Radovan Paška", "radovan@stepuplabs.io", "Step Up Labs", "https://stepuplabs.io")
        developer("David Vávra", "david@stepuplabs.io", "Step Up Labs", "https://stepuplabs.io")
    }
    centralPortalSpec {
        signing.key = secret("MAVEN_CENTRAL_SIGNING_KEY")
        signing.password = secret("MAVEN_CENTRAL_SIGNING_PASSPHRASE")
        auth.user = secret("MAVEN_CENTRAL_UPLOAD_USERNAME")
        auth.password = secret("MAVEN_CENTRAL_UPLOAD_PASSWORD")
        allowMavenCentralSync = false
    }
}