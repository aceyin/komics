package komics.core

import org.junit.Test
import kotlin.test.assertNotNull

/**
 * Created by ace on 16/9/13.
 */
class ApplicationTest {
    val conf_file = "application-test2.yml"

    @Test
    fun test_create_application_context() {
        Application.initialize(arrayOf(""), mapOf<String, String>("conf" to conf_file))
        assertNotNull(Application.context, "application context is null")
    }

}