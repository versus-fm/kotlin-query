package com.github.versusfm.kotlinsql

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.SuspendingConnection
import com.github.versusfm.kotlinsql.adapter.ObjectAdapter
import com.github.versusfm.kotlinsql.annotations.Inject
import com.github.versusfm.kotlinsql.extensions.PostgresExtension
import com.github.versusfm.kotlinsql.query.SelectContext.Companion.from
import com.github.versusfm.kotlinsql.util.ConnectionUtil.Companion.query
import com.github.versusfm.kotlinsql.util.ConnectionUtil.Companion.projectInto
import com.github.versusfm.kotlinsql.util.ConnectionUtil.Companion.queryIntoSequence
import com.github.versusfm.kotlinsql.util.ConnectionUtil.Companion.using
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(PostgresExtension::class)
class ObjectMappedQueryTests {

    private val objectAdapter = ObjectAdapter.createDefaultAdapter()

    data class TestRecord(val id: Long?, val firstName: String, val lastName: String)

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupAll(@Inject connection: SuspendingConnection) {
            runBlocking {
                connection.sendQuery(
                    """
                    create table test_record (
                    id bigserial primary key, 
                    first_name text not null, 
                    last_name text not null
                    );
                    """.trimIndent()
                )
            }

            insert(connection, TestRecord(null, "testFirst", "testLast"))
        }

        private fun insert(connection: SuspendingConnection, record: TestRecord): TestRecord {
            val result = runBlocking {
                connection.sendPreparedStatement(
                    """
                    insert into test_record (first_name, last_name) values (?, ?) returning id, first_name, last_name;
                    """.trimIndent(),
                    listOf(record.firstName, record.lastName),
                    release = true
                ).rows[0]
            }

            return TestRecord(
                result.getLong("id")!!,
                result.getString("first_name")!!,
                result.getString("last_name")!!
            )
        }
    }

    @BeforeEach
    fun setup(@Inject connection: Connection) {
    }

    @Test
    fun testConnection(@Inject connection: Connection) {
        assertNotNull(connection)
    }

    @Test
    fun testSimpleQuery(@Inject connection: SuspendingConnection) {
        assertNotNull(connection)
        val query = from(TestRecord::class.java) {
            selectAll()
            where { id == 1L }
        }
        val result = runBlocking {
            connection.queryIntoSequence(TestRecord::class.java) {
                query
            } using objectAdapter
        }.singleOrNull()


        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("testFirst", result.firstName)
        assertEquals("testLast", result.lastName)

    }


}