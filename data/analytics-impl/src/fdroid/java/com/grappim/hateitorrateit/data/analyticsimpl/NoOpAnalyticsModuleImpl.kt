package com.grappim.hateitorrateit.data.analyticsimpl

import com.grappim.hateitorrateit.data.analyticsapi.AnalyticsController
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpAnalyticsModuleImpl @Inject constructor() : AnalyticsController {
    override fun toggleCrashesCollection(isTurnedOn: Boolean) {
        Timber.d("toggleCrashesCollection: $isTurnedOn")
    }

    override fun toggleAnalyticsCollection(isTurnedOn: Boolean) {
        Timber.d("toggleAnalyticsCollection: $isTurnedOn")
    }

    override fun trackEvent(eventName: String, properties: Map<String, Any>?) {
        Timber.d("trackEvent: $eventName, $properties")
    }
}
