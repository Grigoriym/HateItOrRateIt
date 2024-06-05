package com.grappim.hateitorrateit.data.analyticsapi

interface DetailsAnalytics {
    fun trackDetailsScreenStart()

    fun trackDetailsDeleteProductButtonClicked()

    fun trackDetailsDeleteProductConfirmed()

    fun trackDetailsEditButtonClicked()
}
