package komics.core

import komics.ModuleInitializer
import komics.core.spring.LogInitializer
import komics.core.spring.SpringInitializer
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.wso2.msf4j.spring.MSF4JSpringConfiguration


object Application {
    internal lateinit var opts: Map<String, String>
    internal lateinit var args: Array<String>
    // spring application context
    lateinit var context: AnnotationConfigApplicationContext
    // application config
    lateinit var conf: Config
    // module initializer
    private var moduleInitializer = mutableSetOf<Class<*>>()
    private val LOGGER = LoggerFactory.getLogger(Application::class.java)

    fun start(args: Array<String>, opts: Map<String, String>) {
        LOGGER.info("Initializing application with args: $args and options: $opts")
        this.opts = opts
        this.args = args

        val file = this.opts["conf"] ?: Config.CONF_FILE
        // load config defined in application.yml
        this.conf = Config.load(file)
        // init log4j
        LogInitializer.initLogging()

        // init spring
        this.context = SpringInitializer.createAndInitializeContext()

        // 初始化系统的模块：调用各个模块里面的 ModuleInitializer 的 initialize 方法
        Reflections().getSubTypesOf(ModuleInitializer::class.java)?.forEach { this.moduleInitializer.add(it) }
        this.initializeModules()

        // add MSF4J configuration
        this.context.register(MSF4JSpringConfiguration::class.java)
        // refresh spring context after everything is ready
        this.context.refresh()
    }


    private fun initializeModules() {
        this.moduleInitializer.forEach { clazz ->
            try {
                val listener = clazz.newInstance()
                val method = clazz.getDeclaredMethod("initialize")
                method.invoke(listener)
                LOGGER.info("Calling initialize of '${clazz.name}'")
            } catch (e: Exception) {
                LOGGER.info("Error while calling module initializer: '${clazz.name}'.")
                throw e
            }
        }
        // add shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread() {
            LOGGER.info("Preparing to shutdown module ...")
            this.moduleInitializer.forEach { clazz ->
                try {
                    val listener = clazz.newInstance()
                    val method = clazz.getDeclaredMethod("destroy")
                    method.invoke(listener)
                    LOGGER.info("Calling destroy of '${clazz.name}'")
                } catch (e: Exception) {
                    LOGGER.info("Error while destroy module: '${clazz.name}'.")
                    throw e
                }
            }
        })
    }
}