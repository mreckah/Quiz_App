plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.quiz_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.quiz_app"
        minSdk = 24
        targetSdk = 35
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
}

dependencies {
    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.android.material:material:1.11.0")
    // Firebase libraries
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")


    // AndroidX & Material
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation(libs.play.services.measurement.api)
    implementation(libs.firebase.common)
}
