package com.grappim.hateitorrateit.data.db

fun String.getStringForDbQuery(): String = "%$this%"