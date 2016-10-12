package komics.data.jdbc

import io.kotlintest.specs.ShouldSpec
import komics.data.User
import komics.test.db.DaoTestBase
import java.util.*

/**
 * Created by ace on 2016/10/7.
 */

class DbTest : ShouldSpec() {
    val db = Db(DaoTestBase.datasource)

    override fun beforeAll() {
        DaoTestBase.createTables("/Users/ace/Documents/workspace/git/komics/komics-core/src/test/resources/tables.sql")
    }

    init {
        should("insert_data_for_entity_class_success") {
            val user = User()
            user.id = "11111"
            user.created = Date().time
            user.updated = -1
            user.version = 1

            user.email = "ync@163.com"
            user.password = "123123"
            user.username = "test-user"
            user.mobile = "13800138000"
            user.status = 100

            val inserted = db.insert(user)
            inserted shouldBe true
            val map = DaoTestBase.query("select * from user where id = '11111'")
            map["USERNAME"] shouldBe "test-user"
        }
    }
}