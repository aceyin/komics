package komics.web.template

import org.springframework.stereotype.Controller
import org.wso2.msf4j.template.MustacheTemplateEngine
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by ace on 2016/10/17.
 */
@Controller
@Path("example")
class TemplateExample {

    @GET
    @Path("template/{name}")
    fun hello(@PathParam("name") name: String): Response {
        val map = mapOf("name" to name)
        val html = MustacheTemplateEngine.instance().render("hello.mustache", map);
        return Response.ok()
                .type(MediaType.TEXT_HTML)
                .entity(html)
                .build()
    }
}