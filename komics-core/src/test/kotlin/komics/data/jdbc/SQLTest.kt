package komics.data.jdbc

/**
 * Created by ace on 2016/10/1.
 */
import io.kotlintest.specs.ShouldSpec
import komics.data.EntityMeta
import komics.data.User
import komics.data.jdbc.sql.BT
import komics.data.jdbc.sql.EQ
import komics.data.jdbc.sql.NE
import komics.data.jdbc.sql.SqlBuilder

class SQLTest : ShouldSpec() {
    init {
        should("根据Entity类生成正确的SQL") {
            SqlBuilder.select(User::username, User::id)
                    .from(User::class)
                    .where(User::password EQ 1)
                    .and(User::id NE "2")
                    .and(User::version BT arrayOf(1, 2))
                    .group(User::id, User::username)
                    .order(SqlBuilder.ODR.ASC, User::id, User::email)

            EntityMeta.get(User::class)
        }
    }
}


