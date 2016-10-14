package komics.core

import com.esotericsoftware.yamlbeans.YamlReader
import komics.ConfKeys
import komics.core.spring.DatasourceInitializer
import komics.exception.DataFormatException
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.SimpleCommandLinePropertySource
import org.springframework.core.io.ClassPathResource
import org.wso2.msf4j.spring.MSF4JSpringConfiguration
import java.io.InputStreamReader
import java.util.*

/**
 * Created by ace on 16/9/7.
 */

object Application {
    private val LOGGER = LoggerFactory.getLogger(Application::class.java)
    private val DEFAULT_CONTEXT_CLASS = "org.springframework.context.annotation.AnnotationConfigApplicationContext"
    // spring application context
    lateinit var CONTEXT: ApplicationContext

    fun initialize(args: Array<String>, opts: Map<String, String>) {
        LOGGER.debug("Initializing application with args: $args and options: $opts")
        val file = opts["conf"] ?: Application.Config.CONF_FILE
        // load config defined in application.yml
        Application.Config.load(file)
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
        val configClasses = Application.Config.strs("spring.configurationClasses")
        val pkgScan = Application.Config.strs("spring.packageScan")

        val context = createApplicationContext()
        configApplicationContext(context, configClasses, pkgScan)

        with(context) {
            environment.propertySources.addFirst(SimpleCommandLinePropertySource(*args))
            // put the configuration into spring context
            environment.systemProperties.putAll(Application.Config.PROPS)
            refresh()
        }
        return context
    }

    /**
     * 配置 Spring context
     * @param context
     * @param confClass
     * @param pkgscan
     */
    private fun configApplicationContext(context: ConfigurableApplicationContext, confClass: List<String>, pkgscan: List<String>) {
        val datasourceConf = Application.Config.ORIGIN[ConfKeys.datasource.name]
        if (datasourceConf != null) {
            // 如果发现配置文件中有datasource相关的配置，则初始化datasource和jdbctemplate
            context.addBeanFactoryPostProcessor(DatasourceInitializer(Application.Config))
            // 初始化spring申明式事务支持
        }

        if (context is AnnotationConfigApplicationContext) {
            val classes = mutableSetOf<Class<*>>(MSF4JSpringConfiguration::class.java)
            for (clazz in confClass) classes.add(Class.forName(clazz))
            val pkgs = mutableSetOf<String>(getPackagesForScan()).plus(pkgscan)

            context.register(*classes.toTypedArray())
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

    /**
     * The config of applicatioin
     */
    object Config {
        val CONF_FILE = "conf/application.yml"
        /* 已经转化为: key=value 形式的配置 */
        internal val PROPS: MutableMap<String, Any> = mutableMapOf<String, Any>()
        /* 原始的 yaml 配置 */
        internal val ORIGIN: MutableMap<String, Any> = mutableMapOf<String, Any>()
        private val LOGGER = LoggerFactory.getLogger(Application.Config::class.java.name)

        /**
         * Load the configuration from the given path.
         */
        fun load(path: String) {
            val resource = ClassPathResource(path, Application.Config::class.java.classLoader)
            if (resource.file == null) {
                LOGGER.warn("No $path found , application will start without configuration file.")
                return
            }

            val reader = YamlReader(InputStreamReader(resource.inputStream))
            while (true) {
                val o = reader.read() ?: break
                if (o is HashMap<*, *>) {
                    o.entries.forEach { it -> ORIGIN.put(it.key as String, it.value) }
                    extractMap("", o)
                } else LOGGER.warn("Ignore non-map configuration item: $o")
            }
            reader.close()
        }

        private fun extractMap(preKey: String, map: HashMap<*, *>) {
            val entrySet = map.entries
            for ((k, v) in entrySet) {
                val key = if (preKey.isNullOrBlank()) k else preKey + "." + k
                if (v is HashMap<*, *>) extractMap(key as String, v)
                else if (v is ArrayList<*>) extractList(key as String, v)
                else PROPS.put(key as String, v)
            }
        }

        private fun extractList(prefix: String, list: ArrayList<*>) {
            if (list.size == 0) PROPS.put(prefix, list)
            else if (list[0] is HashMap<*, *>) {
                list.forEach { it ->
                    val p = prefix + "." + (list.indexOf(it) + 1)
                    if (it is HashMap<*, *>) extractMap(p, it)
                    else PROPS.put(p, it)
                }
            } else PROPS.put(prefix, list)
        }

        /**
         * 获取一个字符串列表
         *
         * @param key
         * @return
         */
        fun strs(key: String): List<String> {
            val o = PROPS[key] ?: return emptyList()
            val res = mutableListOf<String>()
            when (o) {
                is ArrayList<*> -> o.forEach {
                    if (it is String) res.add(it)
                    else throw DataFormatException("Value $it for key $key is not a string ")
                }
                is String -> res.add(o)
                else -> throw DataFormatException("Value $o for key $key is not a string ")
            }
            return res
        }

        /**
         * 获取单个字符串配置
         */
        fun str(key: String): String? {
            val v = PROPS[key] ?: return null
            if (v is String) return v
            else throw DataFormatException("Value $v for key $key is not a string ")
        }

        /**
         * 获取单个整形配置
         */
        fun int(key: String): Int? {
            val v = PROPS[key] ?: return null
            if (v is Int) return v
            else if (v is String && v.matches(Regex("\\d+"))) return v.toInt()
            else throw DataFormatException("Value $v for key $key is not a Int ")
        }

        /**
         * 获取整形配置列表
         */
        fun ints(key: String): List<Int>? {
            val v = PROPS[key] ?: return null
            if (v is Int) return listOf(v)
            else if (v is ArrayList<*>) {
                val list = mutableListOf<Int>()
                for (i in v) {
                    if (i is Int) list.add(i)
                    else if (i is String && i.matches(Regex("\\d+"))) list.add(i.toInt())
                    else throw DataFormatException("Value $v for key $key is not a Int ")
                }
                return list
            } else throw DataFormatException("Value $v for key $key is not a int list")
        }

        /**
         * 获取boolean值
         * @param key
         * @return
         */
        fun bool(key: String): Boolean? {
            val o = PROPS.get(key) ?: return null
            if (o is Boolean) return o
            else if (o is String) return o.toBoolean()
            else throw DataFormatException("Value of '$key' is not a boolean type: $o")
        }

        /**
         * 获取boolean列表
         * @param key
         * @return
         */
        fun bools(key: String): List<Boolean> {
            val o = PROPS.get(key) ?: return emptyList<Boolean>()
            if (o is ArrayList<*>) {
                val res = mutableListOf<Boolean>()
                o.forEach { i ->
                    if (i is Boolean) res.add(i)
                    else if (i is String) res.add(i.toBoolean())
                    else throw DataFormatException("Value of '$key' is not a valid boolean: $i")
                }
                return res
            } else if (o is String) return listOf(o.toBoolean())
            else
                throw DataFormatException("Value of '$key' is not a valid boolean: $o")
        }

        /**
         * 获取float值
         * @param key
         * @return
         */
        fun float(key: String): Float? {
            val o = PROPS.get(key) ?: return null
            if (o is Float) return o
            else if (o is String) return o.toFloat()
            else throw DataFormatException("Value of '$key' is not a valid number format: $o")
        }

        /**
         * get float list
         * @param key
         * @return
         */
        fun floats(key: String): List<Float> {
            val o = PROPS.get(key) ?: return emptyList()
            if (o is ArrayList<*>) {
                val res = mutableListOf<Float>()
                o.forEach { i ->
                    if (i is Float) res.add(i)
                    else if (i is String) res.add(i.toFloat())
                    else throw DataFormatException("Value of '$key' is not a valid number format: $i")
                }
                return res
            } else if (o is String) return listOf(o.toFloat())
            throw DataFormatException("Value of '$key' is not a valid number format: $o")
        }
    }
}