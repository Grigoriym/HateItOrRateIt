plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.utils.filesimpl"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":commons"))
    implementation(project(":utils:date-time"))
    implementation(project(":utils:date-time-api"))
    implementation(project(":utils:files-api"))

    implementation(libs.androidx.core.ktx)

    implementation(libs.timber)

    testImplementation(libs.robolectric)
}
