package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugAnalyticsControllerImpl @Inject constructor() : AnalyticsController {
    override fun toggleCrashesCollection(isTurnedOn: Boolean) {
        Timber.d("toggleCrashesCollection isTurnedOn: $isTurnedOn")
    }

    override fun toggleAnalyticsCollection(isTurnedOn: Boolean) {
        Timber.d("toggleAnalyticsCollection isTurnedOn: $isTurnedOn")
    }

    override fun trackEvent(eventName: String, properties: Map<String, Any>?) {
        Timber.d("trackEvent eventName: $eventName, properties: $properties")
    }
}
