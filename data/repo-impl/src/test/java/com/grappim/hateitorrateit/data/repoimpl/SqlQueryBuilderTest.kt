package com.grappim.hateitorrateit.data.repoimpl

import com.grappim.hateitorrateit.data.db.entities.productsTable
import com.grappim.hateitorrateit.domain.HateRateType
import org.junit.Assert.assertEquals
import org.junit.Test

class SqlQueryBuilderTest {

    private val sqlQueryBuilder = SqlQueryBuilder()

    @Test
    fun `buildSqlQuery when query is empty and type is null should return with isCreated and orderClause`() {
        val query = ""
        val type = null

        val result = sqlQueryBuilder.buildSqlQuery(query, type)

        assertEquals(
            "SELECT * FROM $productsTable " +
                    "WHERE isCreated=1 " +
                    "ORDER BY createdDate DESC", result
        )
    }

    @Test
    fun `buildSqlQuery when query is empty and type is not null should return with type, isCreated and orderClause`() {
        val query = ""
        val type = HateRateType.HATE

        val result = sqlQueryBuilder.buildSqlQuery(query, type)

        assertEquals(
            "SELECT * FROM $productsTable " +
                    "WHERE type='HATE' AND isCreated=1 " +
                    "ORDER BY createdDate DESC", result
        )
    }

    @Test
    fun `buildSqlQuery when query is not empty and type is null should return with search query, isCreated and orderClause`() {
        val query = "query"
        val type = null

        val result = sqlQueryBuilder.buildSqlQuery(query, type)

        assertEquals(
            "SELECT * FROM $productsTable " +
                    "WHERE (name LIKE '%query%' OR shop LIKE '%query%' OR description LIKE '%query%') " +
                    "AND isCreated=1 " +
                    "ORDER BY createdDate DESC", result
        )
    }

    @Test
    fun `buildSqlQuery when query is not empty and type is not null should return with search query, type, isCreated and orderClause`() {
        val query = "query"
        val type = HateRateType.HATE

        val result = sqlQueryBuilder.buildSqlQuery(query, type)

        assertEquals(
            "SELECT * FROM $productsTable " +
                    "WHERE (name LIKE '%query%' OR shop LIKE '%query%' OR description LIKE '%query%') " +
                    "AND type='HATE' AND isCreated=1 " +
                    "ORDER BY createdDate DESC", result
        )
    }

    @Test
    fun `buildWhereClause when query is empty and type is null should return with isCreated`() {
        val query = ""
        val type = null

        val result = sqlQueryBuilder.buildWhereClause(query, type)

        assertEquals("WHERE isCreated=1", result)
    }

    @Test
    fun `buildWhereClause when query is empty and type is not null should return with type and isCreated`() {
        val query = ""
        val type = HateRateType.HATE

        val result = sqlQueryBuilder.buildWhereClause(query, type)

        assertEquals("WHERE type='HATE' AND isCreated=1", result)
    }

    @Test
    fun `buildWhereClause when query is not empty and type is null should return with search query and isCreated`() {
        val query = "query"
        val type = null

        val result = sqlQueryBuilder.buildWhereClause(query, type)

        assertEquals(
            "WHERE (name LIKE '%query%' OR shop LIKE '%query%' OR description LIKE '%query%') AND isCreated=1",
            result
        )
    }

    @Test
    fun `buildWhereClause when query is not empty and type is not null should return with search query, type and isCreated`() {
        val query = "query"
        val type = HateRateType.HATE

        val result = sqlQueryBuilder.buildWhereClause(query, type)

        assertEquals(
            "WHERE (name LIKE '%query%' OR shop LIKE '%query%' OR description LIKE '%query%') AND type='HATE' AND isCreated=1",
            result
        )
    }
}