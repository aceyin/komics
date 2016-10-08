package komics.test.db

import io.kotlintest.specs.ShouldSpec

/**
 * Created by ace on 16/9/14.
 */

class H2ServerTest : ShouldSpec() {

    override fun beforeAll() {
        DaoTestBase.createTables("/Users/ace/Documents/workspace/git/komics/komics-test/src/test/resources")
    }

    init {
        should("start h2 server") {

            val conn = DaoTestBase.getConn()

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