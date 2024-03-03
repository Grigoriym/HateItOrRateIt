package com.grappim.hateitorrateit.analyticsapi

interface DetailsAnalytics {
    fun trackDetailsScreenStart()

    fun trackDetailsDeleteProductButtonClicked()

    fun trackDetailsDeleteProductConfirmed()

    fun trackDetailsEditButtonClicked()
}
