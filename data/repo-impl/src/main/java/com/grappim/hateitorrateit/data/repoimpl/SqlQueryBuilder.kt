package com.grappim.hateitorrateit.data.repoimpl

import com.grappim.hateitorrateit.data.db.entities.productsTable
import com.grappim.hateitorrateit.domain.HateRateType
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SqlQueryBuilder @Inject constructor() {

    fun buildSqlQuery(query: String, type: HateRateType?): String {
        val conditions = mutableListOf<String>()

        if (query.isNotEmpty()) {
            val wildcardQuery = query.wrapWithPercentWildcards().wrapWithSingleQuotes()
            conditions.add("(name LIKE $wildcardQuery OR shop LIKE $wildcardQuery OR description LIKE $wildcardQuery)")
        }

        type?.let {
            conditions.add("type=${it.name.wrapWithSingleQuotes()}")
        }

        conditions.add("isCreated=1")

        val whereClause =
            if (conditions.isNotEmpty()) "WHERE " + conditions.joinToString(" AND ") else ""
        val orderByClause = "ORDER BY createdDate DESC"
        val resultQuery = "SELECT * FROM $productsTable $whereClause $orderByClause"
        Timber.d("SQL query: $resultQuery")
        return resultQuery
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
