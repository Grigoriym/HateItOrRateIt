plugins {
    alias(libs.plugins.hateitorrateit.android.library)
}

android {
    namespace = "com.grappim.hateitorrateit.data.cleanerapi"
}

dependencies {
    implementation(project(":domain"))
}
