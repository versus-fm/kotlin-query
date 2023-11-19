package com.github.versusfm.kotlinsql

import com.github.versusfm.kotlinsql.annotation.SqlFunction
import org.junit.jupiter.api.Test
import com.github.versusfm.kotlinsql.annotation.Table
import com.github.versusfm.kotlinsql.query.QueryStubs.Companion.all
import com.github.versusfm.kotlinsql.query.QueryStubs.Companion.contains
import com.github.versusfm.kotlinsql.query.SelectContext
import com.github.versusfm.kotlinsql.query.SelectContext.Companion.from
import kotlin.test.assertEquals

class QueryTests {
    @Table("person")
    data class Person(val id: Long, val firstName: String, val lastName: String)
    @Table("occupation")
    data class Occupation(val id: Long, val personId: Long, val metadata: String)
    @Table("location")
    data class Location(val id: Long, val occupationId: Long, val name: String)



    @Test
    fun testUnconditionalSelect() {
        val query = from(Person::class.java) {
            select { id }
            select { firstName }
            select { lastName }
        }

        val sql = query.compile()
        assertEquals("SELECT \"person\".\"id\", \"person\".\"first_name\", \"person\".\"last_name\" FROM \"person\"", sql)
    }

    @Test
    fun testWhereQuerySelectIdWithParam() {
        val argId = 2L
        val query = from(Person::class.java) {
            select { id }
            where { id == argId }
        }
        val sql = query.compile()
        assertEquals("SELECT \"person\".\"id\" FROM \"person\" WHERE \"person\".\"id\" = :param0", sql)
    }

    @Test
    fun testWhereQuerySelectIdWithConst() {
        val query = from(Person::class.java) {
            select { id }
            where { id == 2L }
        }
        val sql = query.compile()
        assertEquals("SELECT \"person\".\"id\" FROM \"person\" WHERE \"person\".\"id\" = :param0", sql)
    }

    @Test
    fun testOrWhereQuerySelectIdWithConst() {
        val query = from(Person::class.java) {
            select { id }
            where { id == 2L || id == 5L }
        }
        val sql = query.compile()
        assertEquals("SELECT \"person\".\"id\" FROM \"person\" WHERE (\"person\".\"id\" = :param0 OR \"person\".\"id\" = :param1)", sql)
    }

    @Test
    fun testSelectManyField() {
        val query = from(Person::class.java) {
            select { all(id, firstName, lastName) }
            where { id == 2L }
        }
        val sql = query.compile()
        assertEquals("SELECT \"person\".\"id\", \"person\".\"first_name\", \"person\".\"last_name\" FROM \"person\" WHERE \"person\".\"id\" = :param0", sql)
    }

    @Test
    fun testCallCustomFunction() {
        val query = from(Person::class.java) {
            select { all(id, firstName, lower(lastName), lower("test")) }
            where { id == 4L }
        }
        val sql = query.compile()
        assertEquals("SELECT \"person\".\"id\", \"person\".\"first_name\", lower(\"person\".\"last_name\"), lower(:param0) FROM \"person\" WHERE \"person\".\"id\" = :param1", sql)
    }

    @Test
    fun testFunctionCallInCondition() {
        val query = from(Person::class.java) {
            select { id }
            where { firstName == lower("testName") || lastName == lower("testName") }
        }
        val sql = query.compile()
        assertEquals("SELECT \"person\".\"id\" FROM \"person\" WHERE (\"person\".\"first_name\" = lower(:param0) OR \"person\".\"last_name\" = lower(:param0))", sql)
    }

    @SqlFunction
    fun lower(value: String): String {
        return ""
    }

    @Test
    fun testJoin() {
        val query = from(Person::class.java) {
            select { all(id, firstName, lastName) }
            innerJoin(Occupation::class.java) {
                on { person, occupation -> person.id == occupation.personId }
                where { metadata == "test" }
                select { metadata }
            }
        }
        val sql = query.compile()

        assertEquals("SELECT \"person\".\"id\", \"person\".\"first_name\", \"person\".\"last_name\", \"occupation\".\"metadata\" FROM \"person\"  INNER JOIN \"occupation\" ON \"person\".\"id\" = \"occupation\".\"person_id\" WHERE \"occupation\".\"metadata\" = :param0", sql)
    }

    @Test
    fun testNestedJoin() {
        val query = from(Person::class.java) {
            select { all(id, firstName, lastName) }
            innerJoin(Occupation::class.java) {
                on { person, occupation -> person.id == occupation.personId }
                innerJoin(Location::class.java) {
                    on { occupation, location -> occupation.id == location.occupationId }
                    where { name == lower("testLocation") }
                    select { name }
                }
            }
        }
        val sql = query.compile()

        assertEquals("SELECT \"person\".\"id\", \"person\".\"first_name\", \"person\".\"last_name\", \"location\".\"name\" FROM \"person\"  INNER JOIN \"occupation\" ON \"person\".\"id\" = \"occupation\".\"person_id\"  INNER JOIN \"location\" ON \"occupation\".\"id\" = \"location\".\"occupation_id\" WHERE \"location\".\"name\" = lower(:param0)", sql)
    }

    @Test
    fun testSubQuery() {
        val query = from(Person::class.java) {
            select { all(id, firstName, lastName) }
            where { id in from(Occupation::class.java) { select { personId }; where { metadata == "testMeta" } } }
        }
        val sql = query.compile()

        assertEquals("SELECT \"person\".\"id\", \"person\".\"first_name\", \"person\".\"last_name\" FROM \"person\" WHERE \"person\".\"id\" IN (SELECT \"occupation\".\"person_id\" FROM \"occupation\" WHERE \"occupation\".\"metadata\" = :param0)", sql)
    }

}