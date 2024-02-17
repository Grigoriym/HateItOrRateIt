import com.grappim.hateitorrateit.configureJacoco
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationJacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.gradle.jacoco")
                apply("com.android.application")
            }
            configureJacoco()
        }
    }
}
