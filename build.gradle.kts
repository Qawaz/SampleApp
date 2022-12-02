buildscript {
    dependencies {
        val sqldelightVersion = property("sqldelight.version")
        classpath("app.cash.sqldelight:gradle-plugin:$sqldelightVersion")
        classpath("com.google.gms:google-services:4.3.14")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.2")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    kotlin("android").apply(false)
    id("com.google.dagger.hilt.android").apply(false)
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}