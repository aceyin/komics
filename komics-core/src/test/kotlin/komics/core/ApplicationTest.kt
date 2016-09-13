package komics.core

import com.alibaba.druid.pool.DruidDataSource
import com.avaje.ebean.Ebean
import io.kotlintest.specs.ShouldSpec

/**
 * Created by ace on 16/9/13.
 */
class ApplicationTest : ShouldSpec() {
    val conf_file = "application-test2.yml"

    override fun beforeAll() {
        Application.initialize(arrayOf(""), mapOf<String, String>("conf" to conf_file))
    }

    init {
        should("should create datasource bean success") {
            val datasource = Application.CONTEXT!!.getBean("default-datasource")
            (datasource is DruidDataSource) shouldBe true

            val ebean = Ebean.getDefaultServer()
            ebean.name shouldBe "ebean-server-default"
        }
    }
}