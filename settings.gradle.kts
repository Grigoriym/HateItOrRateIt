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
include(":di")
include(":commons")
include(":testing")

include(
    ":data:local-datastorage-api",
    ":data:local-datastorage-impl"
)

include(
    ":data:cleaner-api",
    ":data:cleaner-impl"
)

include(
    ":analytics-impl",
    ":analytics-api"
)

include(
    ":data:remote-config-impl",
    ":data:remote-config-api"
)

include(
    ":data:db",
    ":data:worker-api",
    ":data:worker-impl",
    ":data:repo-api",
    ":data:repo-impl"
)
