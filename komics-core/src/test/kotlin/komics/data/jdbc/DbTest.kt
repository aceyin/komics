package komics.data.jdbc

import io.kotlintest.specs.ShouldSpec
import komics.test.db.DaoTestBase
import java.util.*

/**
 * Created by ace on 2016/10/7.
 */

class DbTest : ShouldSpec() {
    val db = Db(DaoTestBase.datasource)

    init {
        should("insert_data_for_entity_class_success") {
            val user = User("1", 1, Date(), Date(), "Bill")
            val inserted = db.insert(user)
            inserted shouldBe true

            val map = DaoTestBase.query("select * from user where id = 1")

            user.id == map["id"]
        }
    }
}