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

}