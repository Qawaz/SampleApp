pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        kotlin("android").version(kotlinVersion).apply(false)
        id("com.android.application").version(extra["agp.version"] as String).apply(false)
        id("com.android.library").version(extra["agp.version"] as String).apply(false)
        id("com.google.dagger.hilt.android").version(extra["hilt.version"] as String).apply(false)
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "SampleProject"
include(":app")