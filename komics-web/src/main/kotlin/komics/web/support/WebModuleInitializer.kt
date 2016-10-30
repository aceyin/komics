package komics.web.support

import komics.ModuleInitializer
import komics.core.Application
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by ace on 2016/10/23.
 */

open class WebModuleInitializer : ModuleInitializer() {

    private val LOGGER: Logger = LoggerFactory.getLogger(WebModuleInitializer::class.java)

    override fun initialize() {
        LOGGER.info("Initializing Web configurations from ${WebModuleInitializer::class.java.name}")
        Application.context.register(SpringConfig::class.java)
    }

    override fun destroy() {
    }

}