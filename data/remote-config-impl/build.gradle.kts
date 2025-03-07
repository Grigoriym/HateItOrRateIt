plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.data.remoteconfigimpl"
}

dependencies {
    implementation(projects.data.remoteConfigApi)
    implementation(projects.core.async)
    implementation(projects.core.appinfoApi)

    gplayImplementation(platform(libs.firebase.bom))
    gplayImplementation(libs.firebase.config)
    gplayImplementation(libs.firebase.analytics)

    implementation(libs.timber)
}
