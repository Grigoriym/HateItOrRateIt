package com.grappim.hateitorrateit.utils.filesimpl.uri

import android.net.Uri
import com.grappim.hateitorrateit.utils.filesapi.uri.UriParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UriParserImpl @Inject constructor() : UriParser {
    override fun parse(uriString: String): Uri = Uri.parse(uriString)
}
