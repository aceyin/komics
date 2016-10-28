package komics.web.support

import komics.ModuleInitializer
import komics.core.Application
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by ace on 2016/10/23.
 */

open class WebInitializeListener : ModuleInitializer {

    private val LOGGER: Logger = LoggerFactory.getLogger(WebInitializeListener::class.java)

    override fun initialize(application: Application) {
        LOGGER.info("Initializing Web configurations from ${WebInitializeListener::class.java.name}")
        Application.context.register(SpringConfig::class.java)
    }

    override fun destroy(application: Application) {
    }

}