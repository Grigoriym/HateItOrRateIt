plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.utils.filesapi"
}

dependencies {
    implementation(projects.commons)
    implementation(projects.data.repoApi)
}
