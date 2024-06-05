plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.data.cleanerimpl"
}

dependencies {
    implementation(projects.data.cleanerApi)
    implementation(projects.data.repoApi)
    implementation(projects.data.db)
    implementation(projects.core.async)
    implementation(projects.utils.filesApi)

    implementation(libs.timber)

    implementation(libs.androidx.room.runtime)
}
