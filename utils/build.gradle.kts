plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.utils"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":commons"))
    implementation(project(":utils:date-time"))

    coreLibraryDesugaring(libs.android.desugarJdkLibs)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.timber)

    testImplementation(libs.robolectric)
}
