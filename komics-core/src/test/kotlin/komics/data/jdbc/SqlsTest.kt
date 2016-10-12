package komics.data.jdbc

import io.kotlintest.specs.ShouldSpec
import komics.data.Entity
import komics.data.jdbc.sql.Sqls

/**
 * Created by ace on 2016/10/11.
 */
class SqlsTest : ShouldSpec() {
    init {
        should("generate insert sql success") {
            val sql = Sqls.get("komics.data.jdbc.User4Sqls@insert")
            sql shouldBe "INSERT INTO User4Sqls(created,id,name,updated,version) VALUES (:created,:id,:name,:updated,:version)"
        }

        should("generate updateById sql success") {
            val sql = Sqls.get("komics.data.jdbc.User4Sqls@updateById")
            sql shouldBe "UPDATE User4Sqls SET created=:created,name=:name,updated=:updated,version=:version WHERE id=:id"
        }

        should("generate deleteById sql success") {
            val sql = Sqls.get("komics.data.jdbc.User4Sqls@deleteById")
            sql shouldBe "DELETE FROM User4Sqls WHERE id=:id"
        }

        should("generate queryById sql success") {
            val sql = Sqls.get("komics.data.jdbc.User4Sqls@queryById")
            sql shouldBe "SELECT * FROM User4Sqls WHERE id=:id"
        }

        should("use cache when sql is generated before") {
            Sqls.SQL_CACHE.size shouldBe 4
        }
    }
}

data class User4Sqls(val name: String, override var id: String, override var version: Long, override var created: Long, override var updated: Long) : Entity
