package komics.test.db

import io.kotlintest.specs.ShouldSpec

/**
 * Created by ace on 16/9/14.
 */

class H2ServerTest : ShouldSpec() {

    init {
        should("start h2 server") {

            val conn = DaoTestBase.getConn()

            var select = "select 1"

            val pst2 = conn.prepareStatement(select)
            val rs = pst2.executeQuery()
            if (rs.next()) {
                rs.getInt(1) shouldBe 1
            }
            conn.close()
        }
    }
}