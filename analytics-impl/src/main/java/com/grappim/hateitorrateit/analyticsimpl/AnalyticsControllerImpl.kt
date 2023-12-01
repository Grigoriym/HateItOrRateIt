package com.grappim.hateitorrateit.analyticsimpl

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsControllerImpl @Inject constructor() : AnalyticsController {

    override fun toggleCrashesCollection(isTurnedOn: Boolean) {
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(isTurnedOn)
    }
}
