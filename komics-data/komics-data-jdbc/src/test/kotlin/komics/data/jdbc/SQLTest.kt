package komics.data.jdbc

import io.kotlintest.specs.ShouldSpec
import komics.model.Entity
import javax.persistence.Column

/**
 * Created by ace on 2016/10/11.
 */
class SqlTest : ShouldSpec() {
    init {
        should("generate insert sql success") {
            val sql = Sql.get("komics.data.jdbc.User4Sqls@insert")
            sql should startWith("INSERT INTO User4Sqls(id,user_name) VALUES (:id,:name")
        }

        should("generate updateById sql success") {
            val sql = Sql.get("komics.data.jdbc.User4Sqls@updateById")
            sql should startWith("UPDATE User4Sqls SET user_name=:name")
            sql should endWith(" WHERE id=:id")
        }

        should("generate deleteById sql success") {
            val sql = Sql.get("komics.data.jdbc.User4Sqls@deleteById")
            sql shouldBe "DELETE FROM User4Sqls WHERE id=:id"
        }

        should("generate queryById sql success") {
            val sql = Sql.get("komics.data.jdbc.User4Sqls@queryById")
            sql shouldBe "SELECT `id` ,`user_name`  FROM User4Sqls WHERE id=:id"
        }
    }
}

data class User4Sqls(@Column(name = "user_name") val name: String, override var id: String) : Entity
