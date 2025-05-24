plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.dadjokeapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dadjokeapp"
        minSdk = 21
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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Retrofit for API calls
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Room for local database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // RecyclerView
    implementation(libs.recyclerview)
    implementation(libs.room.ktx)
    implementation(libs.okhttp)

    // CardView for flashcard UI
    implementation(libs.cardview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}