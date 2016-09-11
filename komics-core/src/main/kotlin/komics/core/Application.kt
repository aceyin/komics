package komics.core

import com.google.common.collect.Sets
import komics.core.spring.SpringBeanRegistryPostProcessor
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

    /**
     * 手动初始化Spring context环境。
     * 主要过程:
     * - 从 application.yml 文件中读取配置
     * - 将所有配置文件保存到 system environment
     * - 解析 configurationClasses
     * - 解析 packageScan
     * - spring会自动添加 SpringAutoConfig 类作为 Configuration class
     */
    private fun initSpringContext(args: Array<String>): ApplicationContext {
        val configClasses = CONF.strs("spring.configurationClasses")
        val pkgScan = CONF.strs("spring.packageScan")

        val context = createApplicationContext()
        configApplicationContext(context, configClasses, pkgScan)
        context.environment.propertySources.addFirst(SimpleCommandLinePropertySource(*args))
        // put the configuration into spring context
        context.environment.systemProperties.putAll(CONF.PROPS)
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
        context.addBeanFactoryPostProcessor(SpringBeanRegistryPostProcessor(CONF))

        if (context is AnnotationConfigApplicationContext) {
            val classes = mutableSetOf<Class<*>>(MSF4JSpringConfiguration::class.java)

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

    /**
     * 生成 AnnotationConfigApplicationContext 实例
     */
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