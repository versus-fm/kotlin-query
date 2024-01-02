package com.github.versusfm.kotlinsql.extensions

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.SuspendingConnection
import com.github.jasync.sql.db.asSuspending
import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder
import com.github.versusfm.kotlinsql.annotations.Inject
import kotlinx.coroutines.future.await
import org.junit.jupiter.api.extension.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class PostgresExtension : BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback,
    ParameterResolver {
    private val postgisImage = DockerImageName
        .parse("postgis/postgis:16-3.4")
        .asCompatibleSubstituteFor("postgres");
    private var container: PostgreSQLContainer<*> = PostgreSQLContainer(postgisImage)
    private lateinit var connectionPool: ConnectionPool<PostgreSQLConnection>

    private val connections: MutableList<PostgreSQLConnection> = ArrayList()


    override fun beforeAll(context: ExtensionContext?) {
        container.start()
        connectionPool = PostgreSQLConnectionBuilder
            .createConnectionPool(container.getJdbcUrl()) {
                username = container.getUsername()
                password = container.getPassword()
            }
        connectionPool.connect()
    }

    override fun beforeEach(context: ExtensionContext?) {
    }

    override fun afterEach(context: ExtensionContext?) {
        connections.forEach {
            connectionPool.giveBack(it).get()
        }
        connections.clear()
    }

    override fun afterAll(context: ExtensionContext?) {
        container.close()
    }

    override fun supportsParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Boolean {
        return parameterContext?.isAnnotated(Inject::class.java) ?: false
    }

    override fun resolveParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Any {
        if (parameterContext == null)
            throw RuntimeException()
        return if (parameterContext.parameter.type == Connection::class.java) {
            if (connections.isNotEmpty()) {
                return connections.first()
            }
            val connection = connectionPool.take().get()
            connections.add(connection)
            connection
        } else if (parameterContext.parameter.type == SuspendingConnection::class.java) {
            if (connections.isNotEmpty()) {
                return connections.first().asSuspending
            }
            val connection = connectionPool.take().get()
            connections.add(connection)
            connection.asSuspending
        } else {
            throw RuntimeException()
        }
    }

}