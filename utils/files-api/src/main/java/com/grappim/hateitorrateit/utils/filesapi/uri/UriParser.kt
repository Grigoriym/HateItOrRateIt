package com.grappim.hateitorrateit.utils.filesapi.uri

import android.net.Uri

interface UriParser {
    fun parse(uriString: String): Uri
}
