package komics.test.db

import komics.test.db.support.H2Server
import org.h2.jdbcx.JdbcConnectionPool
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.Server
import java.sql.Connection

/**
 * 基于H2数据库测试用例的基类
 */


class DaoTestUtil(a1: String) {

    val a: String = a1

    companion object {

        var connectionPool: JdbcConnectionPool? = null
        var h2server: Server? = null

        fun startDatabase(url: String?, dbName: String?, user: String?, pwd: String?) {
            h2server ?: H2Server.start()

            val isRunning = h2server?.isRunning(true)
            if (isRunning == false) throw RuntimeException("H2 database not started properly")

            val datasource = JdbcDataSource()
            val db = dbName ?: "test"
            val u = user ?: "sa"
            val p = pwd ?: "sa"

            val dbUrl = url ?: "jdbc:h2:mem:$db;DB_CLOSE_DELAY=-1;MODE=MYSQL"
            datasource.setURL(dbUrl)
            datasource.user = u
            datasource.password = p

            connectionPool = JdbcConnectionPool.create(datasource)
        }

        /**
         * 获取一个数据库连接,默认自动提交
         */
        fun conn(autoCommit: Boolean = true): Connection {

            connectionPool?.let {
                val conn = connectionPool.connection
                conn.autoCommit = autoCommit
                return conn
            }
        }
    }
}