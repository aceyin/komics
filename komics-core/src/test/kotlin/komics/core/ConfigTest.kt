package komics.core

import io.kotlintest.specs.ShouldSpec
import java.io.FileNotFoundException
import java.util.*

/**
 * Created by ace on 16/9/12.
 */
class ConfigTest : ShouldSpec() {

    var config = Config.load("application-test3.yml")

    init {
        should("should get property by flat property key successfully") {
            config.strs("spring.packageScan") shouldBe listOf<String>("shenggu")
            config.str("datasource.1.name") shouldBe "default-datasource"
            config.int("datasource.1.minIdle") shouldBe 5
            config.ints("datasource.1.initialSize") shouldBe listOf<Int>(10, 20)
            config.float("datasource.1.maxActive") shouldBe 100.1f
            config.floats("datasource.1.maxActives") shouldBe listOf<Float>(100.1f, 100.2f)
            config.bool("datasource.1.inUse") shouldBe false
            config.bools("datasource.1.inUses") shouldBe listOf<Boolean>(true, false)
        }

        should("should get properties by tree type key successfully") {
            val datasource = config!!.ORIGIN["datasource"]
            val ds = (datasource as ArrayList<*>)[0] as HashMap<String, *>

            ds["name"] as String shouldBe "default-datasource"
            ds["minIdle"] as String shouldBe "5"
        }

        should("throw exception when config file not found") {
            shouldThrow<FileNotFoundException> {
                Config.load("a-non-existance-file.yml") shouldBe Config.EMPTY_CONF
            }
        }
    }
}