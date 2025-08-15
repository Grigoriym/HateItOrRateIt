package com.grappim.hateitorrateit.testing.domain

import kotlin.random.Random

fun getRandomLong(): Long = Random.nextLong()

fun getRandomString(): String = List(15) { // Generate a list of 10 characters
    Random.nextInt(97, 123) // ASCII range for lowercase letters a-z
        .toChar() // Convert ASCII value to char
}.joinToString("")

fun getRandomBoolean(): Boolean = Random.nextBoolean()
