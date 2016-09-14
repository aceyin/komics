package komics.test.db

import io.kotlintest.specs.StringSpec
import org.h2.jdbcx.JdbcConnectionPool
import org.h2.jdbcx.JdbcDataSource

/**
 * Created by ace on 16/9/14.
 */

class H2ServerTest : StringSpec() {
    init {
        "start h2 server" {
            H2Server.start()

            val ds = JdbcDataSource()
            ds.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL")
            ds.user = "sa"
            ds.password = "sa"

            val pool = JdbcConnectionPool.create(ds)
            val conn = pool.getConnection()
            conn.autoCommit = true

            var create = """
            |CREATE TABLE user
            |(
            |id BIGINT(11) PRIMARY KEY AUTO_INCREMENT,
            |username VARCHAR(32) NOT NULL,
            |password VARCHAR(32) NOT NULL,
            |mobile VARCHAR(15) NOT NULL,
            |email VARCHAR(32) NOT NULL,
            |status VARCHAR(20) NOT NULL,
            |version BIGINT(10) DEFAULT '1' NOT NULL,
            |created TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL,
            |modified TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL
            |)
            """.trimMargin()

            val ps1 = conn.prepareStatement(create)
            ps1.executeUpdate()

            var insert = """
            |insert into user (username,password,mobile,email,status,version,created,modified)
            |values (
            |'username','password','mobile','email','status',1,now(),now()
            |)
            """.trimMargin()

            val pst = conn.prepareStatement(insert)
            pst.execute()

            var select = """
            |select * from user
            """.trimMargin()

            val pst2 = conn.prepareStatement(select)
            val rs = pst2.executeQuery()
            if (rs.next()) {
                rs.getString("username") shouldBe "username"
            }
            conn.close()
        }
    }
}