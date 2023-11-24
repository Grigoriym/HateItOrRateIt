package com.grappim.hateitorrateit.data.db

fun String.wrapWithPercentWildcards(): String = "%$this%"

fun String.wrapWithSingleQuotes(): String = "'$this'"