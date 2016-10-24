package komics.web.support

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

/**
 * Created by ace on 2016/10/23.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
open class SpringConfig {
    private val LOGGER: Logger = LoggerFactory.getLogger(SpringConfig::class.java)
}