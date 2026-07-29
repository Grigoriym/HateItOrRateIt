package com.grappim.hateitorrateit

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        compileSdk = libs.findVersion("compileSdk").get().toString().toInt()

        defaultConfig.apply {
            minSdk = libs.findVersion("minSdk").get().toString().toInt()
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21

            isCoreLibraryDesugaringEnabled = true
        }
        packaging.resources.excludes.apply {
            add("META-INF/AL2.0")
            add("META-INF/LGPL2.1")
            add("META-INF/LICENSE.md")
            add("META-INF/LICENSE-notice.md")
        }

        lint.apply {
            checkDependencies = false
            abortOnError = false
            warningsAsErrors = false
        }

        testOptions.apply {
            unitTests {
                isReturnDefaultValues = true
                isIncludeAndroidResources = true
            }
        }
    }

    configureKotlinJvm()

    dependencies {
        "coreLibraryDesugaring"(libs.findLibrary("android.desugarJdkLibs").get())

        "implementation"(libs.findLibrary("kotlinx.collections").get())
        "implementation"(libs.findLibrary("kotlinx.coroutines.core").get())

        add("testImplementation", kotlin("test"))
        add("testImplementation", project(":testing:core"))
        add("testImplementation", project(":testing:domain"))

        add("androidTestImplementation", kotlin("test"))
        add("androidTestImplementation", project(":testing:core"))
        add("androidTestImplementation", project(":testing:domain"))
    }
}

internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21

        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            allWarningsAsErrors.set(false)
            freeCompilerArgs.add("-Xannotation-default-target=first-only")
        }
    }
}
