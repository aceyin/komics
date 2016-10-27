package komics.core

import komics.core.spring.LogInitializer
import komics.core.spring.SpringInitializer
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.wso2.msf4j.spring.MSF4JSpringConfiguration

/**
 * Created by ace on 16/9/7.
 */

object Application {
    internal lateinit var opts: Map<String, String>
    internal lateinit var args: Array<String>
    lateinit var context: AnnotationConfigApplicationContext
    private val systemListeners = arrayOf("komics.data.jdbc.listener.JdbcInitializer",
            "komics.web.support.WebInitializeListener")
    private lateinit var customizedListeners: List<String>
    lateinit var conf: Config

    fun initialize(args: Array<String>, opts: Map<String, String>) {
        println("Initializing application with args: $args and options: $opts")
        this.opts = opts
        this.args = args

        val file = this.opts["conf"] ?: Config.CONF_FILE
        // load config defined in application.yml
        this.conf = Config.load(file)
        // init log4j
        LogInitializer.initLog4J()

        this.customizedListeners = this.conf.strs("application.initialize.listener")
        // init spring
        this.context = SpringInitializer.createAndInitializeContext()
        this.postInitialize()

        // add MSF4J configuration
        this.context.register(MSF4JSpringConfiguration::class.java)
        // refresh spring context after everything is ready
        this.context.refresh()
    }

    private fun postInitialize() {
        // call system level application initialize listener
        this.systemListeners.forEach { callPostInitialize(it) }
        // call customized listener
        this.customizedListeners.forEach { callPostInitialize(it) }
        // add shutdown hook for release resources
        Runtime.getRuntime().addShutdownHook(Thread() {
            println("Preparing to shutdown application ...")
            // call system level application initialize listener
            systemListeners.forEach { callPreShutDown(it) }
            // call customized listener
            customizedListeners.forEach { callPreShutDown(it) }
        })
    }

    private fun callPreShutDown(className: String) {
        try {
            val clazz = Class.forName(className)
            val listener = clazz.newInstance()
            val method = clazz.getDeclaredMethod("preShutdown", javaClass)
            method.invoke(listener, Application)
            println("Calling preShutdown of '$className'")
        } catch (e: ClassNotFoundException) {
            println("System level listener class '$className' not found in class path, corresponding feature disabled.")
        } catch (e: Exception) {
            println("Error while calling listener '$className'.")
            throw e
        }
    }

    private fun callPostInitialize(className: String) {
        try {
            val clazz = Class.forName(className)
            val listener = clazz.newInstance()
            val method = clazz.getDeclaredMethod("postInitialized", javaClass)
            method.invoke(listener, Application)
            println("Calling postInitialized of '$className'")
        } catch (e: ClassNotFoundException) {
            println("System level listener class '$className' not found in class path, corresponding feature disabled.")
        } catch (e: Exception) {
            println("Error while calling listener '$className'.")
            throw e
        }
    }
}