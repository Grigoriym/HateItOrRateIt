package com.grappim.hateitorrateit.data.remoteconfigimpl

data class RemoteConfigDefaults(
    val defaults: Map<String, Any> = mapOf(
        GITHUB_REPO_URL_KEY to "https://github.com/Grigoriym/HateItOrRateIt",
        PRIVACY_POLICY_URL_KEY to "https://github.com/Grigoriym/HateItOrRateIt/wiki/Privacy-Policy"
    )
)
