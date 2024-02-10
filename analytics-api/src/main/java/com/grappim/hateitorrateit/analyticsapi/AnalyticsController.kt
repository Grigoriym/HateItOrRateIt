package com.grappim.hateitorrateit.analyticsapi

interface AnalyticsController {

    fun toggleCrashesCollection(isTurnedOn: Boolean)

    fun toggleAnalyticsCollection(isTurnedOn: Boolean)

    fun trackEvent(eventName: String, properties: Map<String, Any>? = null)
}
