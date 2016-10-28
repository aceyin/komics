package komics.web.support

import komics.core.Application
import komics.web.DefaultExceptionMapper
import komics.web.TestPerson
import org.junit.Ignore
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

/**
 * Created by ace on 2016/10/23.
 */

@Ignore
class WebApiTest {

    companion object {
        init {
            Application.initialize(emptyArray(), mapOf("conf" to "application-rest.yml"))
        }
    }

    @Test
    fun test_form_validate_failed() {
        val t = RestTemplate()

        val headers = HttpHeaders();
        headers.contentType = MediaType.parseMediaType("application/json; charset=UTF-8")
        headers.add("Accept", MediaType.APPLICATION_JSON.toString())

        val form = TestPerson("abc", 201).json()
        val formEntity = HttpEntity<String>(form, headers)

        val s = t.postForObject("http://localhost:8080/rest/test/aspectj", formEntity, String::class.java)
        println(s)

        assertEquals(1, DefaultExceptionMapper.exceptionStatistics.size)
        assertEquals(1, DefaultExceptionMapper.codeStatistics.size)
    }
}