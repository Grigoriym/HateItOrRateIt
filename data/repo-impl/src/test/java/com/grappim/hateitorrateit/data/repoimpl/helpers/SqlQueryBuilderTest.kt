package com.grappim.hateitorrateit.data.repoimpl.helpers

import com.grappim.hateitorrateit.data.db.entities.PRODUCTS_TABLE
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SqlQueryBuilderTest {

    private val sqlQueryBuilder = SqlQueryBuilder()

    private val searchCondition =
        "(name LIKE ? ESCAPE '\\' OR shop LIKE ? ESCAPE '\\' OR description LIKE ? ESCAPE '\\')"

    @Test
    fun `buildSqlQuery when query is empty and type is null should return with isCreated and orderClause`() {
        val query = ""
        val type = null

        val result = sqlQueryBuilder.buildSqlQuery(query, type)

        assertEquals(
            SqlQuery(
                sql = "SELECT * FROM $PRODUCTS_TABLE " +
                    "WHERE isCreated=1 " +
                    "ORDER BY createdDate DESC",
                args = emptyList()
            ),
            result
        )
    }

    @Test
    fun `buildSqlQuery when query is empty and type is not null should return with type, isCreated and orderClause`() {
        val query = ""
        val type = HateRateType.HATE

        val result = sqlQueryBuilder.buildSqlQuery(query, type)

        assertEquals(
            SqlQuery(
                sql = "SELECT * FROM $PRODUCTS_TABLE " +
                    "WHERE type=? AND isCreated=1 " +
                    "ORDER BY createdDate DESC",
                args = listOf("HATE")
            ),
            result
        )
    }

    @Test
    fun `buildSqlQuery when query is not empty and type is null should return with search query, isCreated and orderClause`() {
        val query = "query"
        val type = null

        val result = sqlQueryBuilder.buildSqlQuery(query, type)

        assertEquals(
            SqlQuery(
                sql = "SELECT * FROM $PRODUCTS_TABLE " +
                    "WHERE $searchCondition " +
                    "AND isCreated=1 " +
                    "ORDER BY createdDate DESC",
                args = List(3) { "%query%" }
            ),
            result
        )
    }

    @Test
    fun `buildSqlQuery when query is not empty and type is not null should return with search query, type, isCreated and orderClause`() {
        val query = "query"
        val type = HateRateType.HATE

        val result = sqlQueryBuilder.buildSqlQuery(query, type)

        assertEquals(
            SqlQuery(
                sql = "SELECT * FROM $PRODUCTS_TABLE " +
                    "WHERE $searchCondition " +
                    "AND type=? AND isCreated=1 " +
                    "ORDER BY createdDate DESC",
                args = List(3) { "%query%" } + "HATE"
            ),
            result
        )
    }

    @Test
    fun `buildWhereClause when query is empty and type is null should return with isCreated`() {
        val result = sqlQueryBuilder.buildWhereClause("", null)

        assertEquals(SqlQuery(sql = "WHERE isCreated=1", args = emptyList()), result)
    }

    @Test
    fun `buildWhereClause when query is empty and type is not null should return with type and isCreated`() {
        val result = sqlQueryBuilder.buildWhereClause("", HateRateType.HATE)

        assertEquals(
            SqlQuery(sql = "WHERE type=? AND isCreated=1", args = listOf("HATE")),
            result
        )
    }

    @Test
    fun `buildWhereClause when query is not empty and type is null should return with search query and isCreated`() {
        val result = sqlQueryBuilder.buildWhereClause("query", null)

        assertEquals(
            SqlQuery(
                sql = "WHERE $searchCondition AND isCreated=1",
                args = List(3) { "%query%" }
            ),
            result
        )
    }

    @Test
    fun `buildWhereClause when query is not empty and type is not null should return with search query, type and isCreated`() {
        val result = sqlQueryBuilder.buildWhereClause("query", HateRateType.HATE)

        assertEquals(
            SqlQuery(
                sql = "WHERE $searchCondition AND type=? AND isCreated=1",
                args = List(3) { "%query%" } + "HATE"
            ),
            result
        )
    }

    @Test
    fun `buildSqlQuery when query contains single quote should bind it instead of inlining it`() {
        val result = sqlQueryBuilder.buildSqlQuery("Sam's", null)

        assertFalse(result.sql.contains("Sam"))
        assertEquals(List(3) { "%Sam's%" }, result.args)
    }

    @Test
    fun `buildSqlQuery when query is an injection attempt should keep it out of the sql`() {
        val query = "='"

        val result = sqlQueryBuilder.buildSqlQuery(query, HateRateType.RATE)

        assertFalse(result.sql.contains(query))
        assertEquals(List(3) { "%='%" } + "RATE", result.args)
    }

    @Test
    fun `buildWhereClause when query contains like wildcards should escape them`() {
        val result = sqlQueryBuilder.buildWhereClause("50%_off\\", null)

        assertEquals(List(3) { "%50\\%\\_off\\\\%" }, result.args)
    }
}
