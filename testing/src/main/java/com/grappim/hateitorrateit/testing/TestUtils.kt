package com.grappim.hateitorrateit.testing

import android.net.Uri
import java.time.OffsetDateTime
import kotlin.random.Random

val nowDate = OffsetDateTime.now()

fun getRandomLong(): Long = Random.nextLong()

@Suppress("MagicNumber")
fun getRandomString(): String = List(15) { // Generate a list of 10 characters
    Random.nextInt(97, 123) // ASCII range for lowercase letters a-z
        .toChar() // Convert ASCII value to char
}.joinToString("")

fun getRandomUri(): Uri {
    val randomString = getRandomString()
    val uriString = "https://grappim.com/$randomString"
    return Uri.parse(uriString)
}
