package komics.web

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by ace on 2016/10/28.
 */

class FormTest {

    @Test
    fun test_validate_fail() {
        val form = TestPerson("", 0)
        val result = form.validate()
        assertEquals(false, result.success)
        assertEquals(2, result.errors.size)
        assertEquals("name can not be null", result.errors["name"])
        assertEquals("age should not less than 1", result.errors["age"])

        val form2 = TestPerson("ace", 222)
        val result2 = form2.validate()
        assertEquals(1, result2.errors.size)
        assertEquals("age should not grater than 200", result2.errors["age"])
    }

    @Test
    fun test_validate_success() {
        val form = TestPerson("ace", 20)
        val result = form.validate()
        assertEquals(true, result.success)
    }
}