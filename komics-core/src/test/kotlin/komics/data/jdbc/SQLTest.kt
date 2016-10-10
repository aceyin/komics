package komics.data.jdbc

/**
 * Created by ace on 2016/10/1.
 */
import io.kotlintest.specs.ShouldSpec
import komics.data.EntityMeta
import komics.data.User

class SQLTest : ShouldSpec() {
    init {
        1 and 2
        should("根据Entity类生成正确的SQL") {
            Sql.select(User::name, User::id, User::created)
                    .from(User::class)
                    .where(User::name EQ 1)
                    .and(User::id NE "2")
                    .or(User::updated LT 2)
                    .and(User::version BT arrayOf(1, 2))
                    .or(User::created IN arrayOf(1, 2, 3))

            EntityMeta.get(User::class)
        }
    }
}


