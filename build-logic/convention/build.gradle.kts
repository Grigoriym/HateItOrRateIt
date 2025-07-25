import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.grappim.hateitorrateit.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies{
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.firebase.crashlytics.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "hateitorrateit.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidLibrary") {
            id = "hateitorrateit.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "hateitorrateit.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "hateitorrateit.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("androidApp") {
            id = "hateitorrateit.android.app"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
    }
}
