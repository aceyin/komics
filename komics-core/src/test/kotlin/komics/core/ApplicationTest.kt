package komics.core

import com.alibaba.druid.pool.DruidDataSource
import org.junit.Test
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Created by ace on 16/9/13.
 */
class ApplicationTest {
    val conf_file = "application-test2.yml"

    @Test
    fun test_create_application_context() {
        Application.initialize(arrayOf(""), mapOf<String, String>("conf" to conf_file))
        assertNotNull(Application.CONTEXT)


        val datasource = Application.CONTEXT.getBean("default-datasource")
        assertNotNull(datasource)
        assertTrue(datasource is DruidDataSource)

        val jdbc = Application.CONTEXT.getBean("default-datasourceJdbcTemplate")
        assertTrue(jdbc is JdbcTemplate)
    }

}