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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "HateItOrRateIt"
include(":app")
include(":uikit")

include(
    ":data:local-datastorage-api",
    ":data:local-datastorage-impl"
)

include(
    ":data:cleaner-api",
    ":data:cleaner-impl"
)

include(
    ":data:analytics-impl",
    ":data:analytics-api"
)

include(
    ":data:remote-config-impl",
    ":data:remote-config-api"
)

include(
    ":data:db",
    ":data:repo-api",
    ":data:repo-impl"
)

include(":feature:settings:ui")

include(
    ":core:appinfo-api",
    ":core:async"
)

include(
    ":utils:ui",
    ":utils:files-api",
    ":utils:files-impl",
    ":utils:date-time-api",
    ":utils:date-time"
)
include(":feature:home:ui")
include(":testing:core")
include(":testing:domain")
include(":feature:productmanager:ui")
include(":feature:details:ui")
include(":utils:android-impl")
include(":utils:android-api")
include(":core:app-update-api")
include(":core:app-update-impl")
include(":strings")

check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17)) {
    """
    HateItOrRateIt requires JDK 17+ but it is currently using JDK ${JavaVersion.current()}.
    Java Home: [${System.getProperty("java.home")}]
    https://developer.android.com/build/jdks#jdk-config-in-studio
    """.trimIndent()
}
