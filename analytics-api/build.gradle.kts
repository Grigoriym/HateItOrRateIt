plugins {
    alias(libs.plugins.hateitorrateit.android.library)
}

android {
    namespace = "com.grappim.hateitorrateit.analyticsapi"
}

dependencies {
    implementation(projects.data.repoApi)
}
