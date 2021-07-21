import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
}

val composeVersion = "1.0.0-rc02"

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "io.eugenethedev.taigamobile"
        minSdk = 21
        targetSdk = 30
        versionCode = 13
        versionName = "1.4.2"
        project.base.archivesBaseName = "TaigaMobile-$versionName"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("./keystores/debug.keystore")
            storePassword = "android"
            keyAlias = "debug"
            keyPassword = "android"
        }

        create("release") {
            val properties = Properties().also {
                it.load(file("./signing.properties").inputStream())
            }
            storeFile = file("./keystores/release.keystore")
            storePassword = properties.getProperty("password")
            keyAlias = properties.getProperty("alias")
            keyPassword = properties.getProperty("password")
        }
    }


    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }

    lint { 
        isAbortOnError = false
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.4.0")

    // Main Compose dependencies
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    implementation("androidx.compose.animation:animation:$composeVersion")
    // compose activity
    implementation("androidx.activity:activity-compose:1.3.0-rc02")
    // view model support
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
    // compose constraint layout
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha08")

    // Accompanist
    val accompanistVersion = "0.14.0"
    implementation("com.google.accompanist:accompanist-glide:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")

    // Navigation Component (with Compose)
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha04")

    // ViewModel
    val lifecycleKtxVersion = "2.3.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleKtxVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleKtxVersion")

    // Coroutines
    val coroutinesVersion = "1.5.1-native-mt"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // Retrofit 2
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    // OkHttp
    val okHttpVersion = "4.9.0"
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")

    // Dagger 2
    val daggerVersion = "2.38"
    implementation("com.google.dagger:dagger-android:$daggerVersion")
    kapt("com.google.dagger:dagger-android-processor:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    // Timber
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Markdown support (Markwon)
    val markwonVersion = "4.6.2"
    implementation("io.noties.markwon:core:$markwonVersion")
    implementation("io.noties.markwon:image-glide:$markwonVersion")

    // Compose Material Dialogs
    val composeMaterialDialogsVersion = "0.4.3"
    implementation("io.github.vanpra.compose-material-dialogs:datetime:$composeMaterialDialogsVersion")
    implementation("io.github.vanpra.compose-material-dialogs:color:$composeMaterialDialogsVersion")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}