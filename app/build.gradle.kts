plugins {
    id ("com.android.application")
    kotlin("android")
    id("app.cash.sqldelight")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.wakaztahir.sample"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.wakaztahir.sample"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile ("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = property("compose.compiler.version") as String
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

sqldelight {
    database("SampleDatabase") {
        packageName = "com.wakaztahir.sampleDb"
        schemaOutputDirectory = file("build/dbs")
        sourceFolders = listOf("sqldelight")
        verifyMigrations = true
    }
}

dependencies {

    val composeVersion = property("compose.version")
    val hiltVersion = property("hilt.version")
    val accompanistVersion = property("accompanist.version")
    val sqldelightVersion = property("sqldelight.version")

    // Android Core
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")

    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // SqlDelight
    implementation("app.cash.sqldelight:android-driver:$sqldelightVersion")

    // Hilt
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")

    // Compose
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material3:material3:1.1.0-alpha02")

    // Accompanist
    // implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    // implementation("com.google.accompanist:accompanist-swiperefresh:$accompanistVersion")

    // Billing
    val billingVersion = "5.1.0"
    implementation("com.android.billingclient:billing:$billingVersion")
    implementation("com.android.billingclient:billing-ktx:$billingVersion")

    // Ads
    implementation("com.google.android.gms:play-services-ads:21.3.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:31.0.2"))
    // Crashlytics & Analytics
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Play In-App Review
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    // Testing Compose
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")


}