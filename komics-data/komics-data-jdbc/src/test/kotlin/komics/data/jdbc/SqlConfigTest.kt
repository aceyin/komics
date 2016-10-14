package komics.data.jdbc

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by ace on 2016/10/14.
 */
class SqlConfigTest {

    @Test
    fun test_load_sql() {
        Sql.Config.load("test_sqls.yml")
        val sql1 = Sql.Config.get("abc.def.ghi@insert")
        assertEquals("insert into user (id,name) values (:id,:name)", sql1)

        val sql2 = Sql.Config.get("abc.def.ghi@updateById")
        assertEquals("update user set name=:name where id=:id", sql2)

        val sql3 = Sql.Config.get("some.sql.not.applied.on.entity")
        assertEquals("select x ", sql3)

        val sql4 = Sql.Config.get("multiple.line.sql")
        assertEquals("select x from y where z group by a order by a limit 0,1", sql4)

        val sql5 = Sql.Config.get("report.sqls@sql.1")
        assertEquals("select count * from x", sql5)

        val sql6 = Sql.Config.get("report.sqls@sql.2")
        assertEquals("select 1 from x", sql6)

        val sql7 = Sql.Config.get("multile.level.sql@level1@sql1")
        assertEquals("select a from b", sql7)

        val sql8 = Sql.Config.get("multile.level.sql@level1@sql2")
        assertEquals("select c from d", sql8)

        val sql9 = Sql.Config.get("multile.level.sql@level2@level3@level4@sql4")
        assertEquals("select 4 from 5", sql9)
    }
}