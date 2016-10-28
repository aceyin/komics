package komics.web

import org.springframework.stereotype.Component
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType

/**
 * Created by ace on 2016/10/23.
 */

@Component
@Path("/rest/test")
open class RestApi {
    @POST
    @Path("aspectj")
    @Consumes(MediaType.APPLICATION_JSON)
    fun test_aspectj(@Valid person: TestPerson): String {
        val result = person.validate()
        if (!result.success) throw FormValidationException(person, result)
        return person.json()
    }
}