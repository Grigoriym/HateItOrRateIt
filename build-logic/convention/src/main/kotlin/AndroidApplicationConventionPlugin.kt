import com.android.build.api.dsl.ApplicationExtension
import com.grappim.hateitorrateit.AppBuildTypes
import com.grappim.hateitorrateit.configureFlavors
import com.grappim.hateitorrateit.configureKotlinAndroid
import com.grappim.hateitorrateit.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")

            extensions.configure<ApplicationExtension> {
                defaultConfig.targetSdk = libs.findVersion("targetSdk").get().toString().toInt()

                signingConfigs {
                    getByName("debug") {
                        storeFile = file("../debug.keystore.jks")
                        keyAlias = System.getenv("HIOR_ALIAS_D")
                        keyPassword = System.getenv("HIOR_KEY_PASS_D")
                        storePassword = System.getenv("HIOR_STORE_PASS_D")
                    }

                    create("release") {
                        storeFile = file("../release.keystore.jks")
                        keyAlias = System.getenv("HIOR_ALIAS_R")
                        keyPassword = System.getenv("HIOR_KEY_PASS_R")
                        storePassword = System.getenv("HIOR_STORE_PASS_R")
                        enableV2Signing = true
                        enableV3Signing = true
                    }
                }

                buildTypes {
                    debug {
                        applicationIdSuffix = AppBuildTypes.DEBUG.applicationIdSuffix

                        signingConfig = signingConfigs.getByName("debug")
                    }
                    release {
                        applicationIdSuffix = AppBuildTypes.RELEASE.applicationIdSuffix

                        isMinifyEnabled = true
                        isShrinkResources = true

                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                        signingConfig = signingConfigs.getByName("release")
                    }
                }

                buildFeatures {
                    compose = true

                    buildConfig = true
                }

                bundle {
                    language {
                        // Specifies that the app bundle should not support
                        // configuration APKs for language resources. These
                        // resources are instead packaged with each base and
                        // dynamic feature APK.
                        enableSplit = false
                    }
                }
                androidResources {
                    generateLocaleConfig = true
                }

                configureFlavors(this)
                configureKotlinAndroid(this)
            }
        }
    }
}
