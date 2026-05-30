import com.android.build.api.dsl.LibraryExtension
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
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                configureFlavors(this)
            }
        }
    }
}
