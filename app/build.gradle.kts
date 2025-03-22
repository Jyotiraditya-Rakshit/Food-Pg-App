plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.insightsfromtech.myfoodapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.insightsfromtech.myfoodapp"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
    }

}

dependencies {
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.firebase.storage.ktx)
    val room_version = "2.6.1"
    val lifecycle_version = "2.8.7"
    // Import the BoM for the Firebase platform
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.android.gms:play-services-base:18.4.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")


    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation ("com.google.firebase:firebase-dynamic-links")
    implementation("com.google.firebase:firebase-auth")
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation ("com.github.fashare2015:StackLayout:1.0.0")
    implementation("com.mig35:carousellayoutmanager:1.4.6")
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation ("com.airbnb.android:lottie:6.6.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.airbnb.android:lottie:6.6.2")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}