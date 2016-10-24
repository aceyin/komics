package komics.core

import org.junit.Test
import java.io.FileNotFoundException
import java.util.*
import kotlin.test.assertEquals

/**
 * Created by ace on 16/9/12.
 */
class ConfigTest {

    val conf = Config.load("application-test3.yml")


    @Test
    fun should_get_property_by_flat_property_key_successfully() {
        assertEquals(conf.strs("spring.packageScan"), listOf<String>("komics"))
        assertEquals(conf.str("datasource.1.name"), "default-datasource")
        assertEquals(conf.int("datasource.1.minIdle"), 5)
        assertEquals(conf.ints("datasource.1.initialSize"), listOf<Int>(10, 20))
        assertEquals(conf.float("datasource.1.maxActive"), 100.1f)
        assertEquals(conf.floats("datasource.1.maxActives"), listOf<Float>(100.1f, 100.2f))
        assertEquals(conf.bool("datasource.1.inUse"), false)
        assertEquals(conf.bools("datasource.1.inUses"), listOf<Boolean>(true, false))
    }

    @Test
    fun should_get_properties_by_tree_type_key_successfully() {
        val datasource = conf.ORIGIN["datasource"]
        val ds = (datasource as ArrayList<*>)[0] as HashMap<*, *>

        assertEquals(ds["name"] as String, "default-datasource")
        assertEquals(ds["minIdle"] as String, "5")
    }

    @Test(expected = FileNotFoundException::class)
    fun should_throw_exception_when_config_file_not_found() {
        Config.load("a-non-existance-file.yml")
    }

}