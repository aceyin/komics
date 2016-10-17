package komics.data.listener

import com.alibaba.druid.pool.DruidDataSource
import komics.core.Application
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Created by ace on 2016/10/16.
 */
class JdbcInitializerTest {

    companion object {
        @BeforeClass
        @JvmStatic fun setup() {
            Application.initialize(emptyArray(), mapOf("conf" to "application-test.yml"))
        }
    }

    @Test
    fun test_initialize_jdbc() {

        val datasource = Application.context.getBean("default-datasource")
        assertNotNull(datasource)
        assertTrue(datasource is DruidDataSource, "not found datasource bean")

        val jdbc = Application.context.getBean("default-datasource_JdbcTemplate")
        assertTrue(jdbc is NamedParameterJdbcTemplate, "not found jdbc template bean")

        val trans = Application.context.getBean("default-datasource_TransManager")
        assertTrue(trans is DataSourceTransactionManager, "not found transaction manager bean")
    }
}