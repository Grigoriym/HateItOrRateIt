package com.grappim.hateitorrateit.data.analyticsimpl

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.grappim.hateitorrateit.data.analyticsapi.AnalyticsController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsControllerImpl @Inject constructor() : AnalyticsController {

    override fun toggleCrashesCollection(isTurnedOn: Boolean) {
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = isTurnedOn
    }

    override fun toggleAnalyticsCollection(isTurnedOn: Boolean) {
        Firebase.analytics.setAnalyticsCollectionEnabled(isTurnedOn)
    }

    override fun trackEvent(eventName: String, properties: Map<String, Any>?) {
        val bundle = Bundle().apply {
            properties?.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    else -> throw IllegalArgumentException(
                        "Unsupported type for analytics property"
                    )
                }
            }
        }
        Firebase.analytics.logEvent(eventName, bundle)
    }
}
