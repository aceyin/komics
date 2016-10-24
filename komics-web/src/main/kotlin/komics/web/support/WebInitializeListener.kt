package komics.web.support

import komics.ApplicationListener
import komics.core.Application
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by ace on 2016/10/23.
 */

open class WebInitializeListener : ApplicationListener {

    private val LOGGER: Logger = LoggerFactory.getLogger(WebInitializeListener::class.java)

    override fun postInitialized(application: Application) {
        LOGGER.info("Initializing Web configurations from ${WebInitializeListener::class.java.name}")
        Application.context.register(SpringConfig::class.java)
    }

    override fun preShutdown(application: Application) {
    }

}