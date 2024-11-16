plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.xai.helloworld"
    compileSdk = 34

    buildFeatures {
        compose = true
    }

    defaultConfig {
        applicationId = "com.xai.helloworld"
        minSdk = 34
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    //compose bom
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    // material 3
    implementation(libs.androidx.material3)
    // Android Studio Preview Support
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    // Compose Activity
    implementation(libs.androidx.activity.compose)
}