package komics.test.db

import komics.test.db.support.H2Server
import org.h2.jdbcx.JdbcConnectionPool
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.Server
import java.sql.Connection

/**
 * 基于H2数据库测试用例的基类
 */

interface BaseDatabaseTest {

    companion object {
        val connectionPool: JdbcConnectionPool
        val h2server: Server

        init {
            h2server = H2Server.start()
            val isRunning = h2server.isRunning(true)
            if (!isRunning) throw RuntimeException("H2 database not started properly")

            val datasource = JdbcDataSource()
            datasource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL")
            datasource.user = "sa"
            datasource.password = "sa"

            connectionPool = JdbcConnectionPool.create(datasource)
        }

        /**
         * 获取一个数据库连接,默认自动提交
         */
        fun conn(autoCommit: Boolean = true): Connection {
            val conn = connectionPool.connection
            conn.autoCommit = autoCommit
            return conn
        }
    }
}