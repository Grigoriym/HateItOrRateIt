package com.grappim.hateitorrateit.data.remoteconfigapi

const val GITHUB_REPO_URL = "https://github.com/Grigoriym/HateItOrRateIt"
const val PRIVACY_POLICY_URL = "https://github.com/Grigoriym/HateItOrRateIt/wiki/Privacy-Policy"

data class RemoteConfigDefaults(
    val defaults: Map<String, Any> = mapOf(
        GITHUB_REPO_URL_KEY to GITHUB_REPO_URL,
        PRIVACY_POLICY_URL_KEY to PRIVACY_POLICY_URL,
        IN_APP_UPDATE_ENABLED to true
    )
)
