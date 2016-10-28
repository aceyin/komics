package komics.web

import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.NotEmpty
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by ace on 2016/10/28.
 */

class ExceptionMapperTest {

    @Test
    fun test_invalid_data_exception() {
        val form = Form4TestException("")
        val result = form.validate()

        val ex = FormValidationException(form, result)

        val mapper = DefaultExceptionMapper<Exception>()
        val resp = mapper.toResponse(ex)

        assertEquals(400, resp.status)
        assertEquals(result, resp.entity)
        assertEquals(1, DefaultExceptionMapper.exceptionStatistics[ex.javaClass.name]?.get())
        assertEquals(1, DefaultExceptionMapper.codeStatistics.size)

        val r = mapper.toResponse(NullPointerException())
        assertEquals(500, r.status)
        assertEquals(2, DefaultExceptionMapper.exceptionStatistics.size)
        assertEquals(2, DefaultExceptionMapper.codeStatistics.size)

    }
}

internal class Form4TestException(
        @get:NotEmpty
        @get:Length(min = 20, max = 40)
        val name: String
) : Form