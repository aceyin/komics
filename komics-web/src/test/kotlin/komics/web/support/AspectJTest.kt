package komics.web.support

import komics.core.Application
import komics.web.TestForm
import org.junit.Ignore
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

/**
 * Created by ace on 2016/10/23.
 */

@Ignore
class AspectJTest {

    companion object {
        init {
            Application.initialize(emptyArray(), mapOf("conf" to "application-rest.yml"))
        }
    }

    @Test
    fun should_enable_aspectj_success() {
        val t = RestTemplate()

        val headers = HttpHeaders();
        headers.contentType = MediaType.parseMediaType("application/json; charset=UTF-8")
        headers.add("Accept", MediaType.APPLICATION_JSON.toString())

        val form = TestForm("abc", 10).json()
        val formEntity = HttpEntity<String>(form, headers)

        val s = t.postForObject("http://localhost:8080/rest/test/aspectj", formEntity, String::class.java)
        println(s)
    }
}