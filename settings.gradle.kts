pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// https://issuetracker.google.com/issues/315023802#comment18
gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))

rootProject.name = "HateItOrRateIt"
include(":app")
include(":ui")
include(":domain")
include(":utils")
include(":data:db")
include(":data:worker-api")
include(":data:worker-impl")
include(":data:repo-api")
include(":data:repo-impl")
include(":di")
include(":commons")
include(":data:local-datastorage-api")
include(":data:local-datastorage-impl")
include(":data:cleaner-api")
include(":data:cleaner-impl")
include(":testing")
include(":analytics-impl")
include(":analytics-api")
include(":data:remote-config-impl")
include(":data:remote-config-api")
