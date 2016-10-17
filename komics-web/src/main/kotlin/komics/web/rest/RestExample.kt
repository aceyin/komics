package komics.web.rest

import org.springframework.stereotype.Controller
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam

/**
 * Created by ace on 2016/10/17.
 */
@Controller
@Path("/example")
class RestExample {
    @GET
    @Path("rest/{name}")
    fun hello(@PathParam("name") name: String): String {
        return "Hello, $name"
    }
}