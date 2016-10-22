package komics.web.rest

import org.springframework.stereotype.Controller
import javax.ws.rs.GET
import javax.ws.rs.Path

/**
 * Created by ace on 2016/10/21.
 */

@Controller
@Path("/example")
class RestApi {

    @GET
    @Path("hello")
    fun hello(): String {
        return "hello"
    }
}