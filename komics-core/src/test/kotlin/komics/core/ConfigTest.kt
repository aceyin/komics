package komics.core

import org.junit.Test
import java.io.FileNotFoundException
import java.util.*
import kotlin.test.assertEquals

/**
 * Created by ace on 16/9/12.
 */
class ConfigTest {

    init {
        Application.Config.load("application-test3.yml")
    }

    val config = Application.Config

    @Test
    fun should_get_property_by_flat_property_key_successfully() {
        assertEquals(config.strs("spring.packageScan"), listOf<String>("shenggu"))
        assertEquals(config.str("datasource.1.name"), "default-datasource")
        assertEquals(config.int("datasource.1.minIdle"), 5)
        assertEquals(config.ints("datasource.1.initialSize"), listOf<Int>(10, 20))
        assertEquals(config.float("datasource.1.maxActive"), 100.1f)
        assertEquals(config.floats("datasource.1.maxActives"), listOf<Float>(100.1f, 100.2f))
        assertEquals(config.bool("datasource.1.inUse"), false)
        assertEquals(config.bools("datasource.1.inUses"), listOf<Boolean>(true, false))
    }

    @Test
    fun should_get_properties_by_tree_type_key_successfully() {
        val datasource = config!!.ORIGIN["datasource"]
        val ds = (datasource as ArrayList<*>)[0] as HashMap<String, *>

        assertEquals(ds["name"] as String, "default-datasource")
        assertEquals(ds["minIdle"] as String, "5")
    }

    @Test(expected = FileNotFoundException::class)
    fun should_throw_exception_when_config_file_not_found() {
        Application.Config.load("a-non-existance-file.yml")
    }

    @Test
    fun test_load_sql() {
        SqlConfig.load("test_sqls.yml")
        val sql1 = SqlConfig.get("abc.def.ghi@insert")
        assertEquals("insert into user (id,name) values (:id,:name)", sql1)

        val sql2 = SqlConfig.get("abc.def.ghi@updateById")
        assertEquals("update user set name=:name where id=:id", sql2)

        val sql3 = SqlConfig.get("some.sql.not.applied.on.entity")
        assertEquals("select x ", sql3)

        val sql4 = SqlConfig.get("multiple.line.sql")
        assertEquals("select x from y where z group by a order by a limit 0,1", sql4)

        val sql5 = SqlConfig.get("report.sqls@sql.1")
        assertEquals("select count * from x", sql5)

        val sql6 = SqlConfig.get("report.sqls@sql.2")
        assertEquals("select 1 from x", sql6)

        val sql7 = SqlConfig.get("multile.level.sql@level1@sql1")
        assertEquals("select a from b", sql7)

        val sql8 = SqlConfig.get("multile.level.sql@level1@sql2")
        assertEquals("select c from d", sql8)

        val sql9 = SqlConfig.get("multile.level.sql@level2@level3@level4@sql4")
        assertEquals("select 4 from 5", sql9)
    }
}