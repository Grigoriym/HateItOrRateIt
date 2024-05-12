package com.grappim.hateitorrateit.utils.filesimpl

import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

interface UriParser {
    fun parse(uriString: String): Uri
}

@Singleton
class UriParserImpl @Inject constructor() : UriParser {
    override fun parse(uriString: String): Uri = Uri.parse(uriString)
}
