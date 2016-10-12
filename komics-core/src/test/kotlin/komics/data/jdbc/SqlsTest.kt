package komics.data.jdbc

import io.kotlintest.specs.ShouldSpec
import komics.data.User
import komics.data.jdbc.sql.Sqls

/**
 * Created by ace on 2016/10/11.
 */
class SqlsTest : ShouldSpec() {
    init {
        should("generate insert sql success") {
            val sql = Sqls.get(User::class, Sqls.SqlType.INSERT)
            sql shouldBe "INSERT INTO user(_version,created,name,updated,uuid) VALUES (:_version,:created,:name,:updated,:uuid)"
        }
    }
}