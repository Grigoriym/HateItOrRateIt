plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.data.repoimpl"
}

dependencies {
    implementation(project(":utils:files-api"))
    implementation(project(":utils:date-time-api"))
    implementation(projects.commons)
    implementation(project(":data:repo-api"))
    implementation(project(":data:db"))
    implementation(project(":data:local-datastorage-api"))

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.sqlite)

    implementation(libs.timber)
}
