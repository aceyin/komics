package komics.test.db

import io.kotlintest.specs.StringSpec

/**
 * Created by ace on 16/9/14.
 */

class H2ServerTest : DaoTestUtil, StringSpec() {
    init {
        "start h2 server" {

            val conn = DaoTestUtil.conn()

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