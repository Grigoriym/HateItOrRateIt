package com.grappim.hateitorrateit.data.repoimpl

import androidx.annotation.VisibleForTesting
import com.grappim.hateitorrateit.data.db.entities.PRODUCTS_TABLE
import com.grappim.hateitorrateit.domain.HateRateType
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SqlQueryBuilder @Inject constructor() {

    fun buildSqlQuery(query: String, type: HateRateType?): String {
        val whereClause = buildWhereClause(query, type)
        val orderByClause = "ORDER BY createdDate DESC"
        val resultQuery = "SELECT * FROM $PRODUCTS_TABLE $whereClause $orderByClause"
        Timber.d("SQL query: $resultQuery")
        return resultQuery
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun buildWhereClause(query: String, type: HateRateType?): String {
        val conditions = mutableListOf<String>()

        if (query.isNotEmpty()) {
            val wrappedQuery = query.wrapWithPercentWildcards().wrapWithSingleQuotes()
            conditions.add(
                "(name LIKE $wrappedQuery " +
                    "OR shop LIKE $wrappedQuery " +
                    "OR description LIKE $wrappedQuery)"
            )
        }

        type?.let {
            conditions.add("type=${it.name.wrapWithSingleQuotes()}")
        }

        conditions.add("isCreated=1")

        return "WHERE ${conditions.joinToString(" AND ")}"
    }

    /**
     * Finds any values that have "query" in any position
     */
    private fun String.wrapWithPercentWildcards(): String = "%$this%"

    /**
     * It is needed to wrap string with single quotes to avoid SQL syntax errors
     */
    private fun String.wrapWithSingleQuotes(): String = "'$this'"
}
