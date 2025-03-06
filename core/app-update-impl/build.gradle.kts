plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.core.appupdateimpl"
}

dependencies {
    implementation(projects.core.appUpdateApi)

    gplayImplementation(libs.google.inAppUpdateKtx)

    implementation(libs.timber)
}
