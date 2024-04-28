plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.utils.datetime"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":commons"))

    coreLibraryDesugaring(libs.android.desugarJdkLibs)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.timber)

    testImplementation(libs.robolectric)
}
