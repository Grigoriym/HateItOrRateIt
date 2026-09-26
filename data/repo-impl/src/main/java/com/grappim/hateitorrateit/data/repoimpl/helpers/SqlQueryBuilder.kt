package com.grappim.hateitorrateit.data.repoimpl.helpers

import androidx.annotation.VisibleForTesting
import com.grappim.hateitorrateit.data.db.entities.PRODUCTS_TABLE
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import timber.log.Timber
import javax.inject.Inject

class SqlQueryBuilder @Inject constructor() {

    fun buildSqlQuery(query: String, type: HateRateType?): SqlQuery {
        val whereClause = buildWhereClause(query, type)
        val orderByClause = "ORDER BY createdDate DESC"
        val resultQuery = "SELECT * FROM $PRODUCTS_TABLE ${whereClause.sql} $orderByClause"
        Timber.d("SQL query: $resultQuery")
        return SqlQuery(sql = resultQuery, args = whereClause.args)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun buildWhereClause(query: String, type: HateRateType?): SqlQuery {
        val conditions = mutableListOf<String>()
        val args = mutableListOf<Any>()

        if (query.isNotEmpty()) {
            conditions.add(
                "(name LIKE ? ESCAPE '\\' " +
                    "OR shop LIKE ? ESCAPE '\\' " +
                    "OR description LIKE ? ESCAPE '\\')"
            )
            val pattern = query.escapeLikeWildcards().wrapWithPercentWildcards()
            repeat(SEARCHABLE_COLUMNS_COUNT) { args.add(pattern) }
        }

        type?.let {
            conditions.add("type=?")
            args.add(it.name)
        }

        conditions.add("isCreated=1")

        return SqlQuery(sql = "WHERE ${conditions.joinToString(" AND ")}", args = args)
    }

    private fun String.escapeLikeWildcards(): String = this
        .replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_")

    private fun String.wrapWithPercentWildcards(): String = "%$this%"

    private companion object {
        const val SEARCHABLE_COLUMNS_COUNT = 3
    }
}
