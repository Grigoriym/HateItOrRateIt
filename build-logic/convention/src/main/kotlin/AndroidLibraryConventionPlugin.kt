import com.android.build.gradle.LibraryExtension
import com.grappim.hateitorrateit.configureFlavors
import com.grappim.hateitorrateit.configureKotlinAndroid
import com.grappim.hateitorrateit.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
                configureFlavors(this)
            }
        }
    }
}
