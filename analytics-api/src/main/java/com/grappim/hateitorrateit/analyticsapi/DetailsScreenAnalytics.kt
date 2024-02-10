package com.grappim.hateitorrateit.analyticsapi

interface DetailsScreenAnalytics {
    fun trackDetailsScreenStart()

    fun trackDetailsDeleteProductButtonClicked()

    fun trackDetailsDeleteProductConfirmed()

    fun trackDetailsEditButtonClicked()
}
