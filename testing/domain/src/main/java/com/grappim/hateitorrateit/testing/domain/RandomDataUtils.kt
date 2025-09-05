package com.grappim.hateitorrateit.testing.domain

import kotlin.random.Random

fun getRandomLong(): Long = Random.nextLong()

fun getRandomString(): String = List(20) {
    Random.nextInt(97, 123) // ASCII range for lowercase letters a-z
        .toChar() // Convert ASCII value to char
}.joinToString("")

fun getRandomBoolean(): Boolean = Random.nextBoolean()
