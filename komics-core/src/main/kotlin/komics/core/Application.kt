package komics.core

import com.google.common.collect.Sets
import komics.core.conf.SpringAutoConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.SimpleCommandLinePropertySource
import org.wso2.msf4j.spring.MSF4JSpringConfiguration

/**
 * Created by ace on 16/9/7.
 */

object Application {
    private val LOGGER = LoggerFactory.getLogger(Application::class.java)
    private val DEFAULT_CONTEXT_CLASS = "org.springframework.context.annotation.AnnotationConfigApplicationContext"
    // system configurations
    var CONF = Config.EMPTY_CONF
    // spring application context
    var CONTEXT: ApplicationContext? = null

    fun initialize(args: Array<String>) {
        CONF = Config.load(Config.CONF_FILE)
        CONTEXT = initSpringContext(args)
    }

    private fun initSpringContext(args: Array<String>): ApplicationContext {
        val context = createApplicationContext()
        val configClasses = CONF.strs("spring.configurationClasses")
        val pkgScan = CONF.strs("spring.packageScan")

        configApplicationContext(context, configClasses, pkgScan)
        context.environment.propertySources.addFirst(SimpleCommandLinePropertySource(*args))
        // put the configuration into spring context
        context.environment.systemProperties.putAll(CONF.HOLDER)
        context.refresh()
        return context
    }

    /**
     * 配置 Spring context
     * @param context
     * @param confClass
     * @param pkgscan
     */
    private fun configApplicationContext(context: ConfigurableApplicationContext, confClass: List<String>, pkgscan: List<String>) {
        if (context is AnnotationConfigApplicationContext) {
            val classes = mutableSetOf(MSF4JSpringConfiguration::class.java, SpringAutoConfig::class.java)
            for (clazz in confClass) classes.add(Class.forName(clazz))
            context.register(*classes.toTypedArray())

            val pkgs = Sets.newHashSet<String>(getPackagesForScan())
            for (p in pkgscan) pkgs.add(p)
            context.scan(*pkgs.toTypedArray())
        }
    }

    private fun getPackagesForScan(): String {
        return this.javaClass.`package`.name
    }

    internal fun createApplicationContext(): ConfigurableApplicationContext {
        try {
            val clazz = Class.forName(DEFAULT_CONTEXT_CLASS)
            return BeanUtils.instantiate(clazz) as ConfigurableApplicationContext
        } catch (ex: ClassNotFoundException) {
            throw IllegalStateException(
                    "Unable to create a default ApplicationContext, please specify an ApplicationContextClass", ex)
        }
    }
}