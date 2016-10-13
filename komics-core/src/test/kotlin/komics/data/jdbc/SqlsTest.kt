package komics.data.jdbc

import io.kotlintest.specs.ShouldSpec
import komics.data.Entity
import komics.data.jdbc.sql.Sqls
import javax.persistence.Column

/**
 * Created by ace on 2016/10/11.
 */
class SqlsTest : ShouldSpec() {
    init {
        should("generate insert sql success") {
            val sql = Sqls.get("komics.data.jdbc.User4Sqls@insert")
            sql shouldBe "INSERT INTO User4Sqls(id,user_name,version) VALUES (:id,:name,1)"
        }

        should("generate updateById sql success") {
            val sql = Sqls.get("komics.data.jdbc.User4Sqls@updateById")
            sql should startWith("UPDATE User4Sqls SET user_name=:name,version=version+1 WHERE id=:id")
            sql should endWith(" WHERE id=:id")
        }

        should("generate deleteById sql success") {
            val sql = Sqls.get("komics.data.jdbc.User4Sqls@deleteById")
            sql shouldBe "DELETE FROM User4Sqls WHERE id=:id"
        }

        should("generate queryById sql success") {
            val sql = Sqls.get("komics.data.jdbc.User4Sqls@queryById")
            sql shouldBe "SELECT `id` ,`user_name` ,`version`  FROM User4Sqls WHERE id=:id"
        }

        should("use cache when sql is generated before") {
            Sqls.SQL_CACHE.size shouldBe 4
        }
    }
}

data class User4Sqls(@Column(name = "user_name") val name: String, override var id: String, override var version: Long) : Entity
